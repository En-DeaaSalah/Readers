package wifi.codewl.readers.model;

import wifi.codewl.readers.model.channel.Channel;

public class User {


    private static User userReference;


    private User() {


    }


    public static User getUserReference() {

        if (userReference == null) {
            userReference = new User();

        }


        return userReference;


    }


    private String userId;

    private String userName;

    private Account account;

    private Channel channel;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
