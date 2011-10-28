
package de.cebitec.mgx.client.data;

/**
 *
 * @author sjaenick
 */
public class User {

    private final String user;
    private final char[] password;

    public User(String user, char[] password) {
        this.user = user;
        this.password = password;
    }

    public char[] getPassword() {
        return password;
    }

    public String getName() {
        return user;
    }
}
