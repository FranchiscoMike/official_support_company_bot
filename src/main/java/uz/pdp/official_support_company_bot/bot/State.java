package uz.pdp.official_support_company_bot.bot;

public interface State {
    String START = "starting point!";

    String FULL_NAME = "entering full name of person";

    String PHONE_NUMBER = "entering phone number";

    String POSITION = "entering position!";

    String EMAIL = "entering email";
    String CHECKOUT = "Checking all details";

    String ENTERING_CODE = "Code checking for developer role";

    String MENU = "Showing menu";
    String SETTING_WEEKLY_TARGET = "Setting his or her weekly target";
    String SENDING_NEWS = "Sending news to all users of bot!";

    String SEND_TO_ALL = "Sending to all users";

    String CHOOSE_ONE = "choosing one to send message to one person!";

    String SEND_TO_ONE = "Sending message to one person!";

    String USER_OR_ADMIN = "Checking user or admin for menu bar!";

    String COMMENTING = "Writing messages to admins!";
    String PERSONAL_MESSAGE = "Sending to one special message!";
    String SEND_PRIVATE_MESSAGE = "sending private message1";

    String EDITING_TARGET = "editing his or her target!";
    String SETTING_WEEKLY_TARGET_RESULTS = "setting results of weekly target!";

}
