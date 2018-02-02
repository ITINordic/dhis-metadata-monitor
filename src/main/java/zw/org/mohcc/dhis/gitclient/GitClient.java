/* 
 * Copyright (c) 2018, ITINordic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package zw.org.mohcc.dhis.gitclient;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff.StageState;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;

/**
 *
 * @author cliffordc
 */
public class GitClient {

    private interface OpenOrCreateRepoResult {

        Git getGit();

        boolean isNewRepo();
    }

    private static void listUncommitedChages(Git git) throws IOException, GitAPIException {
                    Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Listing uncommitted changes:");
        Status status = git.status().call();
        Set<String> conflicting = status.getConflicting();
        for (String conflict : conflicting) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Conflicting: " + conflict);
        }

        Set<String> added = status.getAdded();
        for (String add : added) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Added: " + add);
            
        }

        Set<String> changed = status.getChanged();
        for (String change : changed) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Change: " + change);
        }

        Set<String> missing = status.getMissing();
        for (String miss : missing) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Missing: " + miss);
        }

        Set<String> modified = status.getModified();
        for (String modify : modified) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Modification: " + modify);
        }

        Set<String> removed = status.getRemoved();
        for (String remove : removed) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Removed: " + remove);
        }

        Set<String> uncommittedChanges = status.getUncommittedChanges();
        for (String uncommitted : uncommittedChanges) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Uncommitted: " + uncommitted);
        }

        Set<String> untracked = status.getUntracked();
        for (String untrack : untracked) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Untracked: " + untrack);
        }

        Set<String> untrackedFolders = status.getUntrackedFolders();
        for (String untrack : untrackedFolders) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "Untracked Folder: " + untrack);
        }

        Map<String, StageState> conflictingStageState = status.getConflictingStageState();
        for (Map.Entry<String, StageState> entry : conflictingStageState.entrySet()) {
            Logger.getLogger(GitClient.class.getName()).log(Level.INFO, "ConflictingState: " + entry);
        }
    }
// source: https://stackoverflow.com/questions/23486483/file-diff-against-the-last-commit-with-jgit

    private static boolean diff(Git git, OutputStream diffStream) throws IOException {
        boolean hasDiff = false;
        DiffFormatter formatter = new DiffFormatter(diffStream);
        formatter.setRepository(git.getRepository());
        AbstractTreeIterator commitTreeIterator = prepareTreeParser(git.getRepository(), Constants.HEAD);
        FileTreeIterator workTreeIterator = new FileTreeIterator(git.getRepository());
        List<DiffEntry> diffEntries = formatter.scan(commitTreeIterator, workTreeIterator);
        formatter.setOldPrefix("old/");
        formatter.setNewPrefix("new/");
        for (DiffEntry entry : diffEntries) {
            hasDiff = true;
            formatter.format(entry);
        }
        return hasDiff;
    }

    // source: https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowBranchDiff.java
    private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        Ref head = repository.exactRef(ref);
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    public String process(Path gitDirectory) throws GitProcessingFailedException {
        OpenOrCreateRepoResult result;
        try {
            result = openOrCreateRepo(gitDirectory);
            ByteArrayOutputStream diffStream = null;
            Git git = result.getGit();
            if (result.isNewRepo()) {
                addAndCommit(git, "initial commit");

            } else {
                // Diff changes
                listUncommitedChages(git);
                diffStream = new ByteArrayOutputStream();
                if (diff(git, diffStream)) {
                    addAndCommit(git, "update");
                }

            }
            return diffStream == null ? "" : diffStream.toString();

        } catch (IOException | GitAPIException ex) {
            Logger.getLogger(GitClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new GitProcessingFailedException(ex);
        }
    }

    private static void addAndCommit(Git git, String msg) throws GitAPIException {
        // Add if there are changes
        git.add().addFilepattern(".").call();
        final String msgTimestamp = msg + ": " + LocalDateTime.now();
        // Commit if there are changes
        git.commit().setMessage(msgTimestamp).call();
    }

    private OpenOrCreateRepoResult openOrCreateRepo(Path gitDirectory) throws IOException, GitAPIException {
        Git git;
        boolean isNewRepoTmp = false;

        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.addCeilingDirectory(gitDirectory.toFile());
        repositoryBuilder.findGitDir(gitDirectory.toFile());
        if (repositoryBuilder.getGitDir() == null) {
            git = Git.init().setDirectory(gitDirectory.toFile()).call();
            isNewRepoTmp = true;
        } else {
            git = new Git(repositoryBuilder.build());
        }

        final boolean isNewRepo = isNewRepoTmp;

        return new OpenOrCreateRepoResult() {
            @Override
            public Git getGit() {
                return git;
            }

            @Override
            public boolean isNewRepo() {
                return isNewRepo;
            }
        };
    }
}
