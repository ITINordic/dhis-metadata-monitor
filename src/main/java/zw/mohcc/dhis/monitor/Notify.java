/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.monitor;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author cliffordc
 * 
 */

@Data
@RequiredArgsConstructor
public class Notify {
    @NonNull private DataSetGroupConfig group;
    private Set<String> messages = new HashSet<>();
}
