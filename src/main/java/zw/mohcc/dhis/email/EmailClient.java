/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zw.mohcc.dhis.email;

/**
 *
 * @author cliffordc
 */
public interface EmailClient {
    public void sendMessage(
            String from, String recipients[],
            String subject, String message
    );
}
