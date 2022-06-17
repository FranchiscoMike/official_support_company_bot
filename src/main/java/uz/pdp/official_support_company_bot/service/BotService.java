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
import uz.pdp.official_support_company_bot.entity.enums.MessageType;
import uz.pdp.official_support_company_bot.repository.BotUserRepository;
import uz.pdp.official_support_company_bot.repository.MessagesRepository;

import java.time.LocalDate;
import java.time.LocalTime;
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

        if (text.contains("@") && text.trim().length() > 2) {
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
                    String target = current_user.getWeekly_target() == null ? "\uD83E\uDD37\uD83C\uDFFB\u200D♂️ Hali target o'rnatilmagan!" : current_user.getWeekly_target();

                    InlineKeyboardButton button = new InlineKeyboardButton(botUser.getFullName() + " | " + target + " | " + botUser.getPosition());
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

                String target = current_user.getWeekly_target() == null ? "\uD83E\uDD37\uD83C\uDFFB\u200D♂️ Hali target o'rnatilmagan!" : current_user.getWeekly_target();

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


        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(7);

        current_user.setWeekly_target(text + "\ncreated at\t" + LocalDate.now() + " " + (LocalTime.now() + "").substring(0, 8));
        current_user.setState(State.USER_OR_ADMIN);
        userRepository.save(current_user);

        sendMessage.setText("Successfully set weekly target please be aware of meeting your target!\uD83D\uDE09 ");
        return sendMessage;
    }


    public SendMessage user_or_admin(Update update, BotUser current_user, String code) {

        Message message = update.getMessage();
        String entering_code = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        sendMessage.setText("Please choose from menu below!");
        if (entering_code.equals(code)) {
            // admin menu
            current_user.setRole("admin");
            current_user.setState(State.USER_OR_ADMIN);
            userRepository.save(current_user);

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            List<KeyboardRow> rowList = new ArrayList<>();

            KeyboardRow row0 = new KeyboardRow();
            row0.add(new KeyboardButton(Button.ALL_TARGETS));
            row0.add(new KeyboardButton(Button.INBOX));
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
            current_user.setState(State.USER_OR_ADMIN);
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

    private final MessagesRepository messagesRepository;

    public SendMessage admin_menu_bar(Update update, BotUser current_user) {

        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rowList = new ArrayList<>();

        KeyboardRow row0 = new KeyboardRow();
        row0.add(new KeyboardButton(Button.ALL_TARGETS));
        row0.add(new KeyboardButton(Button.INBOX));
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


        System.out.println("text = " + text);

        switch (text) {


            case Button.ALL_PEOPLE:


                // hamma odamlarni bittalab replymark up qilamiza nasib bo'lsa

                ReplyKeyboardMarkup replyKeyboardMarkup1 = new ReplyKeyboardMarkup();
                replyKeyboardMarkup1.setResizeKeyboard(true);
                replyKeyboardMarkup1.setSelective(true);
                replyKeyboardMarkup1.setOneTimeKeyboard(true);

                List<KeyboardRow> rowList12 = new ArrayList<>();


                List<BotUser> all = userRepository.findAll();

                for (BotUser botUser : all) {
                    String s = botUser.getFullName() + " | " + botUser.getPhoneNumber();

                    KeyboardRow row12 = new KeyboardRow();
                    row12.add(new KeyboardButton(s));
                    rowList12.add(row12);
                }


                KeyboardRow row2 = new KeyboardRow();
                row2.add(new KeyboardButton(Button.BACK));
                rowList12.add(row2);

                replyKeyboardMarkup1.setKeyboard(rowList12);
                sendMessage.setReplyMarkup(replyKeyboardMarkup1);
                sendMessage.setText("Choose one from menu !");

                // personal message yuborish uchun
                current_user.setState(State.PERSONAL_MESSAGE);
                userRepository.save(current_user);
                break;

            case Button.NEWS:
                sendMessage.setText("Please enter text of news fo sending it to all users!");
                current_user.setState(State.SENDING_NEWS);
                userRepository.save(current_user);
                // user endi nasib bo'lsa
                break;
            case Button.ALL_TARGETS:
                String sb = "\t\t\tHozirgacha bo'lgan targetlar\n\n\n";

                List<BotUser> userList = userRepository.findAll();

                for (BotUser user : userList) {
                    String xabar = user.getFullName() + " | " + user.getPhoneNumber() + " | " + user.getPosition() + " \nTarget : " + user.getWeekly_target() + "\n\n";
                    sb += xabar;
                }

                sendMessage.setText(sb);
                break;
            case Button.BACK:

                current_user.setState(State.USER_OR_ADMIN);
                sendMessage.setText("Select one from menu bar!");
                userRepository.save(current_user);
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

                String target = current_user.getWeekly_target() == null ? "\uD83E\uDD37\uD83C\uDFFB\u200D♂️ Hali target o'rnatilmagan!" : current_user.getWeekly_target();

                profile += "\n\n\uD83D\uDCCC Your weekly target is " + target;

                sendMessage.setText(profile);
                break;
            case Button.INBOX:

                // kimdan yuborilgani qachon yuborilgani bo'lishi kerak

//                Olimjon : xabari bo'ladi at time

                String s = "";
                List<Messages> messages1 = messagesRepository.findAll();

                for (Messages mess : messages1) {
                    if (mess.getType().equals(MessageType.COMMENT)) {
                        s += (mess.getSender().getFullName() + " : " + mess.getText() + "\n\n");
                    }
                }

                sendMessage.setText(s);
                break;
            default:
                sendMessage.setText("Wrong message is sent");

        }


        return sendMessage;
    }

    public SendMessage user_menu_bar(Update update, BotUser current_user) {

        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


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
        row.add(new KeyboardButton(Button.EDIT_TARGET));
        rowList.add(row);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(Button.SETTING_WEEKLY_RESULTS));
        rowList.add(keyboardRow);


        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        switch (text) {
            case Button.COMMENT:
                sendMessage.setText("Please enter your comment there!");
                current_user.setState(State.COMMENTING);
                userRepository.save(current_user);
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

                String target = current_user.getWeekly_target() == null ? "\uD83E\uDD37\uD83C\uDFFB\u200D♂️ Hali target o'rnatilmagan!" : current_user.getWeekly_target();

                profile += "\n\n\uD83D\uDCCC Your weekly target is " + target;

                sendMessage.setText(profile);
                break;
            case Button.EDIT_TARGET:

                if (!current_user.isTargetEdited()) {
                    sendMessage.setText("Yangi targetingizni kiriting \n\n" +
                            "\uD83D\uDD0A NOTICE : Targetni faqat 1 marta edit qila olishiz mumkin!");
                    // olingan xabar bilan to'g'ri editing messagega boradi nasib bo'lsa
                    current_user.setState(State.EDITING_TARGET);
                } else {
                    sendMessage.setText("Siz avval target edit qilib bo'lgansiz bu imkoniyatdan boshqa foydalana olmaysiz!");
                }
                userRepository.save(current_user);
                break;

            case Button.SETTING_WEEKLY_RESULTS:

                sendMessage.setText("Ushbu hafta mobaynida qilgan targetlariz haqida qisqacha ma'lumot bering");
                current_user.setState(State.SETTING_WEEKLY_TARGET_RESULTS);
                userRepository.save(current_user);

                // bundan kelgan xabar nasib  bo'lsa statelarga boradi


                break;
        }


        return sendMessage;
    }

    public SendMessage commenting(Update update, BotUser current_user) {

        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        Messages messages = new Messages();

        messages.setType(MessageType.COMMENT);
        messages.setSender(current_user);
        messages.setText(text + " at " + LocalDate.now() + " " + (LocalTime.now() + "").substring(0, 8)); // shuni yoniga time qo'shilsa yaxshi bo'ladi

        messagesRepository.save(messages);

        sendMessage.setText("Thanks for your feedback!");

        return sendMessage;
    }

    public SendMessage checkingNews(Update update, BotUser current_user) {
        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        Messages messages = new Messages();

//        messages.setType(MessageType.COMMENT); // o'rnatilmaydi tekshirish uchun
        messages.setSender(current_user);
        messages.setText(text);

        messagesRepository.save(messages);

        sendMessage.setText("Would you like to send this message  !\n\n" +
                "message : " + text);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rowList = new ArrayList<>();


        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(Button.ACCEPT));
        row1.add(new KeyboardButton(Button.REJECT));
        rowList.add(row1);


        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);


        return sendMessage;
    }

    public SendMessage sendingPersonalMessage(Update update, BotUser current_user) {

        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        String[] split = text.split(" \\| ");
        String name = split[0];


        Optional<BotUser> byFullName = userRepository.findByFullName(name);
        BotUser botUser = byFullName.get();

        // admin tanlagan odam keladi :
        Messages messages = new Messages();
        messages.setReceiver(botUser);
        messages.setSender(current_user);
        messages.setType(MessageType.PRIVATE);

        messagesRepository.save(messages);


//        Optional<Messages> bySender_idAndReceiver_idAndType = messagesRepository.findBySender_IdAndReceiver_IdAndType(current_user.getId(), botUser.getId(), MessageType.PRIVATE.name());
//
//        Messages messages = bySender_idAndReceiver_idAndType.get();


        sendMessage.setText(botUser.getFullName() + "ga yubormoqchi bo'lgan xabaringizni kiriting");


        return sendMessage;
    }

    public SendMessage editingTarget(Update update, BotUser current_user) {
        Message message = update.getMessage();
        String new_target = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        current_user.setWeekly_target(new_target + "\n" +
                "created at\t" + LocalDate.now() + " " + LocalTime.now());
        current_user.setTargetEdited(true);
        userRepository.save(current_user);
        // agar save qilinmasa buni o'zgartirmaydi javada!
        sendMessage.setText("Your target is updated successfully!");


        return sendMessage;
    }

    public SendMessage settingWeeklyTargetResults(Update update, BotUser current_user) {

        Message message = update.getMessage();
        String target_result = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));


        // agar target o'rnatilgan bo'lsa unga result yozadi agar result yozsa demak u taskni yakunlagan bo'ladi nasib bo'lsa

        return sendMessage;
    }
}
