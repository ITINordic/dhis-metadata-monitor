/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.gitclient;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
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
        System.out.println("Listing uncommitted changes:");
        Status status = git.status().call();
        Set<String> conflicting = status.getConflicting();
        for (String conflict : conflicting) {
            System.out.println("Conflicting: " + conflict);
        }

        Set<String> added = status.getAdded();
        for (String add : added) {
            System.out.println("Added: " + add);
        }

        Set<String> changed = status.getChanged();
        for (String change : changed) {
            System.out.println("Change: " + change);
        }

        Set<String> missing = status.getMissing();
        for (String miss : missing) {
            System.out.println("Missing: " + miss);
        }

        Set<String> modified = status.getModified();
        for (String modify : modified) {
            System.out.println("Modification: " + modify);
        }

        Set<String> removed = status.getRemoved();
        for (String remove : removed) {
            System.out.println("Removed: " + remove);
        }

        Set<String> uncommittedChanges = status.getUncommittedChanges();
        for (String uncommitted : uncommittedChanges) {
            System.out.println("Uncommitted: " + uncommitted);
        }

        Set<String> untracked = status.getUntracked();
        for (String untrack : untracked) {
            System.out.println("Untracked: " + untrack);
        }

        Set<String> untrackedFolders = status.getUntrackedFolders();
        for (String untrack : untrackedFolders) {
            System.out.println("Untracked Folder: " + untrack);
        }

        Map<String, StageState> conflictingStageState = status.getConflictingStageState();
        for (Map.Entry<String, StageState> entry : conflictingStageState.entrySet()) {
            System.out.println("ConflictingState: " + entry);
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
