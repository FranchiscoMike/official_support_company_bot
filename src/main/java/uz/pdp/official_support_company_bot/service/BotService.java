package uz.pdp.official_support_company_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.official_support_company_bot.bot.Button;
import uz.pdp.official_support_company_bot.bot.State;
import uz.pdp.official_support_company_bot.entity.BotUser;
import uz.pdp.official_support_company_bot.entity.Messages;
import uz.pdp.official_support_company_bot.repository.BotUserRepository;
import uz.pdp.official_support_company_bot.repository.PinMessagesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BotService {

    private final BotUserRepository userRepository;

    public SendMessage welcome(Update update) {

        SendMessage sendMessage = new SendMessage();

        Message message = update.getMessage();
        Long chatId = message.getChatId();

        sendMessage.setChatId(chatId + "");

        sendMessage.setText("In order to use full of services please enter your full name");

        return sendMessage;
    }

    public SendMessage welcome_oldUser(Update update) {

        SendMessage sendMessage = new SendMessage();

        Message message = update.getMessage();
        Long chatId = message.getChatId();

        sendMessage.setChatId(chatId + "");

        sendMessage.setText("It is not your state please enter what asked !!!");

        return sendMessage;
    }

    public SendMessage askingEmail(Update update, BotUser current_user) {

        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();

        current_user.setFullName(text);
        userRepository.save(current_user);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Thank you " + text + "\nNow Please enter your email example :gmail@gmail.com");
        return sendMessage;
    }

    public SendMessage askingPhoneNumber(Update update, BotUser current_user) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));

        if (text.contains("@gmail.com") && text.trim().length() > 10) {
            // true email is coming
            current_user.setEmail(text);
            userRepository.save(current_user);
            sendMessage.setText("Please share your phone number!");

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            List<KeyboardRow> rowList = new ArrayList<>();

            KeyboardButton button = new KeyboardButton("Share Phone number");
            button.setRequestContact(true);

            KeyboardRow row = new KeyboardRow();
            row.add(button);
            rowList.add(row);


            replyKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

        } else {
            sendMessage.setText("Please enter in valid type!");
        }
        return sendMessage;
    }

    public SendMessage askingTheirPosition(Update update, BotUser current_user) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        Contact contact = message.getContact();
        String phoneNumber = contact.getPhoneNumber();
        current_user.setPhoneNumber(phoneNumber);
        userRepository.save(current_user);

        sendMessage.setText("In what position are you working in Support Solutions Company!");
        return sendMessage;
    }

    public SendMessage showingProfile(Update update, BotUser current_user) {

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        String position = message.getText();
        current_user.setPosition(position);
        BotUser save = userRepository.save(current_user);

        sendMessage.setText(save.getFullName() + " | " + save.getPosition() + " | " + save.getPhoneNumber());

        // 2 ta button for accepting or rejecting :
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add(new KeyboardButton(Button.ACCEPT));
        row.add(new KeyboardButton(Button.REJECT));


        rowList.add(row);
        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public SendMessage asking_code(Update update, BotUser current_user) {
        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        String xabar = "";

        if (text.equals(Button.ACCEPT)) {
            // menu buttons are showed!
//            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//            replyKeyboardMarkup.setResizeKeyboard(true);
//            replyKeyboardMarkup.setSelective(true);
//            replyKeyboardMarkup.setOneTimeKeyboard(true);
//
//            List<KeyboardRow> rowList = new ArrayList<>();
//            KeyboardRow row = new KeyboardRow();
//
//            row.add(new KeyboardButton(Button.ALL_PEOPLE));
//            row.add(new KeyboardButton(Button.NEWS));
//            rowList.add(row);
//
//            KeyboardRow row1 = new KeyboardRow();
//            row1.add(new KeyboardButton(Button.WEEKLY_TARGET));
//            row1.add(new KeyboardButton(Button.PROFILE));
//
//            rowList.add(row1);
//            replyKeyboardMarkup.setKeyboard(rowList);
//            sendMessage.setReplyMarkup(replyKeyboardMarkup);

            xabar = "Enter the code";


        } else if (text.equals(Button.REJECT)) {
            userRepository.delete(current_user);
            xabar = "All right your details are cleared now you have to enter again!\n" +
                    "Plese click /start!";
        } else {
            xabar = "please enter one button!";
        }

        sendMessage.setText(xabar);

        return sendMessage;
    }

    public SendMessage allUsers(Update update, BotUser current_user) {
        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        switch (text) {
            case Button.ALL_PEOPLE:
                sendMessage.setText("Qaysi userga xabar yubormoqchisiz ?\uD83E\uDDD0");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

                List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

                List<BotUser> all = userRepository.findAll();

                for (BotUser botUser : all) {
                    String target = current_user.getWeekly_target() == null ?"\uD83E\uDD37\uD83C\uDFFB\u200D♂️ Hali target o'rnatilmagan!": current_user.getWeekly_target();

                    InlineKeyboardButton button = new InlineKeyboardButton(botUser.getFullName()+" | "+target+" | "+botUser.getPosition());
                    button.setCallbackData(botUser.getChatId());
                    List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
                    keyboardButtons.add(button);
                    buttons.add(keyboardButtons);
                }
                inlineKeyboardMarkup.setKeyboard(buttons);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                current_user.setState(State.CHOOSE_ONE);
                userRepository.save(current_user);

                break;
            case Button.NEWS:
                sendMessage.setText("Please enter text of news fo sending it to all users!");
                current_user.setState(State.SENDING_NEWS);
                userRepository.save(current_user);
                // user endi nasib bo'lsa
                break;
            case Button.WEEKLY_TARGET:

                String weekly_target = current_user.getWeekly_target();
                if (weekly_target == null) {
                    sendMessage.setText("Please enter your weekly taget!");
                    current_user.setState(State.SETTING_WEEKLY_TARGET);
                    userRepository.save(current_user);
                } else {
                    sendMessage.setText("\uD83D\uDCCC Your weekly target is " + current_user.getWeekly_target());
                }


                break;
            case Button.PROFILE:
                String profile = "";

                profile += "\uD83D\uDC68\u200D\uD83D\uDCBC Your name is " + current_user.getFullName();
                profile += "\n\n\uD83D\uDCF2 your phone number : " + current_user.getPhoneNumber();
                profile += "\n\n\uD83D\uDC68\uD83C\uDFFD\u200D\uD83D\uDCBB position : " + current_user.getPosition();

                String target = current_user.getWeekly_target() == null ?"\uD83E\uDD37\uD83C\uDFFB\u200D♂️ Hali target o'rnatilmagan!": current_user.getWeekly_target();

                profile += "\n\n\uD83D\uDCCC Your weekly target is " + target;

                sendMessage.setText(profile);
                break;


        }

        return sendMessage;

    }

    public SendMessage setWeeklyTarget(Update update, BotUser current_user) {
        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        current_user.setWeekly_target(text);
        current_user.setState(State.MENU);
        userRepository.save(current_user);

        sendMessage.setText("Successfully set weekly target please be aware of meeting your target!\uD83D\uDE09 ");
        return sendMessage;
    }

    private final PinMessagesRepository pinMessagesRepository;


    public SendMessage choosing_one(Update update, BotUser current_user) {
        Message message = update.getCallbackQuery().getMessage();
        Long chatId = message.getChatId();

        String sending_chatId = message.getText();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(sending_chatId));

        Optional<BotUser> byChatId = userRepository.findByChatId(sending_chatId);

//        BotUser botUser = byChatId.get();

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();

        System.out.println(data);


        if (update.hasCallbackQuery()){
            CallbackQuery query = update.getCallbackQuery();
            String text = query.getMessage().getText();
            System.out.println("text = " + text);
        }
        return sendMessage;
    }


    public SendMessage user_or_admin(Update update, BotUser current_user, String code) {

        Message message = update.getMessage();
        String entering_code = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        sendMessage.setText("Please choose from menu below!");
        if (entering_code.equals(code)){
            // admin menu
            current_user.setRole("admin");
            userRepository.save(current_user);

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            List<KeyboardRow> rowList = new ArrayList<>();

            KeyboardRow row0 = new KeyboardRow();
            row0.add(new KeyboardButton(Button.ALL_TARGETS));
            rowList.add(row0);


            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(Button.ALL_PEOPLE));
            row.add(new KeyboardButton(Button.NEWS));
            rowList.add(row);

            KeyboardRow row1 = new KeyboardRow();
            row1.add(new KeyboardButton(Button.WEEKLY_TARGET));
            row1.add(new KeyboardButton(Button.PROFILE));
            rowList.add(row1);


            replyKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);



        } else {
            // user menu
            current_user.setRole("user");
            userRepository.save(current_user);


            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            List<KeyboardRow> rowList = new ArrayList<>();



            KeyboardRow row1 = new KeyboardRow();
            row1.add(new KeyboardButton(Button.WEEKLY_TARGET));
            row1.add(new KeyboardButton(Button.PROFILE));
            rowList.add(row1);

            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(Button.COMMENT));
            rowList.add(row);


            replyKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

        }

        return sendMessage;
    }

    public SendMessage admin_menu_bar(Update update, BotUser current_user) {

        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        switch (text) {
            case Button.ALL_PEOPLE:
                break;
            case Button.NEWS:
                break;
            case Button.ALL_TARGETS:
                break;
            case Button.WEEKLY_TARGET:
                break;
            case Button.PROFILE:
                break;
            case Button.INBOX:
                break;
            default:

        }



        return null;
    }

    public SendMessage user_menu_bar(Update update, BotUser current_user) {

        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        switch (text) {
            case Button.COMMENT :
                sendMessage.setText("Please enter your comment there!");


            case Button.WEEKLY_TARGET:

                String weekly_target = current_user.getWeekly_target();
                if (weekly_target == null) {
                    sendMessage.setText("Please enter your weekly taget!");
                    current_user.setState(State.SETTING_WEEKLY_TARGET);
                    userRepository.save(current_user);
                } else {
                    sendMessage.setText("\uD83D\uDCCC Your weekly target is " + current_user.getWeekly_target());
                }


                break;
            case Button.PROFILE:
                String profile = "";

                profile += "\uD83D\uDC68\u200D\uD83D\uDCBC Your name is " + current_user.getFullName();
                profile += "\n\n\uD83D\uDCF2 your phone number : " + current_user.getPhoneNumber();
                profile += "\n\n\uD83D\uDC68\uD83C\uDFFD\u200D\uD83D\uDCBB position : " + current_user.getPosition();

                String target = current_user.getWeekly_target() == null ?"\uD83E\uDD37\uD83C\uDFFB\u200D♂️ Hali target o'rnatilmagan!": current_user.getWeekly_target();

                profile += "\n\n\uD83D\uDCCC Your weekly target is " + target;

                sendMessage.setText(profile);
                break;

        }


        return null;
    }

    public SendMessage commenting(Update update, BotUser current_user) {
        return null;
    }
}
