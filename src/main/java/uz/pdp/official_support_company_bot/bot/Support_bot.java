package uz.pdp.official_support_company_bot.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.official_support_company_bot.entity.BotUser;
import uz.pdp.official_support_company_bot.entity.Messages;
import uz.pdp.official_support_company_bot.entity.enums.MessageType;
import uz.pdp.official_support_company_bot.repository.BotUserRepository;
import uz.pdp.official_support_company_bot.repository.MessagesRepository;
import uz.pdp.official_support_company_bot.service.BotService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class Support_bot extends TelegramLongPollingBot {


    @Value("${telegram_bot_username}")
    String username;
    @Value("${telegram_bot_botToken}")
    String botToken;

    @Value("${special_code}")
    String code;


    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    private final BotService botService;
    private final BotUserRepository userRepository;

    private final MessagesRepository messagesRepository;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            // user kirgan bo'ladi shu userni aniqlanadi :
            BotUser current_user = null;

            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId + "");
            sendMessage.setText("12 ");

            Optional<BotUser> optionalBotUser = userRepository.findByChatId(chatId.toString());

            if (optionalBotUser.isPresent()) {
                current_user = optionalBotUser.get();

                String state = current_user.getState();


                switch (state) {
                    case State.FULL_NAME:
                        sendMessage.setText(" ");
                        sendMessage = botService.askingEmail(update, current_user);
                        current_user.setState(State.EMAIL);
                        userRepository.save(current_user);
                        break;
                    case State.EMAIL:
                        sendMessage = botService.askingPhoneNumber(update, current_user);
                        if (!sendMessage.getText().equals("Please enter in valid type!")) {
                            current_user.setState(State.PHONE_NUMBER);
                            userRepository.save(current_user);
                        }
                        break;
                    case State.PHONE_NUMBER:
                        if (message.hasContact()) {
                            sendMessage = botService.askingTheirPosition(code,update, current_user);
                            current_user.setState(State.POSITION);
                            userRepository.save(current_user);
                        } else {
                            sendMessage.setText("Please share your your contact for more info!");
                        }
                        break;
                    case State.POSITION:
                        sendMessage = botService.showingProfile(update, current_user);
                        current_user.setState(State.CHECKOUT);
                        userRepository.save(current_user);
                        break;
                    case State.CHECKOUT:
                        sendMessage = botService.asking_code(update, current_user);
                        break;
                    case State.ENTERING_CODE:
                        sendMessage = botService.user_or_admin(update, current_user, code);
                        current_user.setState(State.USER_OR_ADMIN);
                        userRepository.save(current_user);
                        break;


                    case State.USER_OR_ADMIN:

                        String role = current_user.getRole();

                        switch (role) {
                            case "admin":
                                sendMessage = botService.admin_menu_bar(update, current_user);
                                break;
                            case "user":
                                sendMessage = botService.user_menu_bar(update, current_user);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + state);
                        }
                        break;
                    case State.PERSONAL_MESSAGE:

                        sendMessage = botService.sendingPersonalMessage(update, current_user);
                        current_user.setState(State.SEND_PRIVATE_MESSAGE);
                        userRepository.save(current_user);
                        break;


                    case State.SEND_PRIVATE_MESSAGE:
                        // xabarni shu yerda jo'natamiza bo'lmasa ishlameydi

                        Optional<Messages> bySender_idAndTypeAndTextIsNull = messagesRepository.findBySender_IdAndTypeAndTextIsNull(current_user.getId(), MessageType.PRIVATE);
                        Messages messages1 = bySender_idAndTypeAndTextIsNull.get();

                        messages1.setText(text);
                        messagesRepository.save(messages1);

                        // userga xabar jo'natiladi
                        BotUser receiver = messages1.getReceiver();

                        sendMessage.setChatId(receiver.getChatId());
                        sendMessage.setText(text + "\n\nby : " + current_user.getFullName());

                        execute(sendMessage);


//                        adminga jo'natadigan xabar :
                        sendMessage.setText("xabaringiz yuborildi !");
                        sendMessage.setChatId(current_user.getChatId());
                        execute(sendMessage);
                        current_user.setState(State.USER_OR_ADMIN);
                        userRepository.save(current_user);
                        break;


                    case State.COMMENTING:
                        // comment yuboriladi HR ga
                        sendMessage = botService.commenting(update, current_user);
                        current_user.setState(State.USER_OR_ADMIN);
                        userRepository.save(current_user);
                        break;

                    case State.SETTING_WEEKLY_TARGET:
                        sendMessage = botService.setWeeklyTarget(update, current_user);
                        break;


                    case State.CHECKING_TARGET:
                        sendMessage = botService.chekingTarget(update, current_user);
                        break;



                    case State.SENDING_NEWS:
                        sendMessage = botService.checkingNews(update, current_user);
                        current_user.setState(State.SEND_TO_ALL);
                        userRepository.save(current_user);
                        break;

                    case State.SEND_TO_ALL:

                        Messages message1 = new Messages();

                        // finding message
                        Optional<Messages> optionalMessages = messagesRepository.findBySender_IdAndTypeIsNull(current_user.getId());
                        if (optionalMessages.isPresent()) {
                            Messages messages = optionalMessages.get();
                            message1 = messages;
                            messages.setType(MessageType.PUBLIC);
                            messagesRepository.save(messages);

                        }

                        switch (text) {
                            case Button.ACCEPT:
                                List<BotUser> all = userRepository.findAll();
                                for (BotUser botUser : all) {
                                    sendMessage.setText(message1.getText()+"\n\n by :"+current_user.getFullName());
                                    sendMessage.setChatId(botUser.getChatId());
                                    execute(sendMessage);
                                }
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

                                    KeyboardRow row3 = new KeyboardRow();
                                    row3.add(new KeyboardButton(Button.SHOW_ALL_TARGET_HISTORY));
                                    rowList.add(row3);


                                    replyKeyboardMarkup.setKeyboard(rowList);
                                    sendMessage.setReplyMarkup(replyKeyboardMarkup);



                                break;
                            case Button.REJECT:

                                messagesRepository.delete(message1);
                                sendMessage.setText("Data cleared successfully!");
                                break;
                        }


                        sendMessage.setText("Message is sent to all users");
                        current_user.setState(State.USER_OR_ADMIN);
                        userRepository.save(current_user);
                        break;
                    case State.EDITING_TARGET:
                        sendMessage = botService.editingTarget(update,current_user);
                        current_user.setState(State.USER_OR_ADMIN);
                        userRepository.save(current_user);
                        break;


                    case State.SETTING_WEEKLY_TARGET_RESULTS:
                        sendMessage = botService.settingWeeklyTargetResults(update,current_user);
                        current_user.setState(State.USER_OR_ADMIN);
                        userRepository.save(current_user);
                        break;


                    default:
                        sendMessage.setText("Something went wrong please enter what is asked!, " + current_user.getFullName());
                }

            } else {
                if (text.equals("/start")) {
                    sendMessage = botService.welcome(update);
                }

                BotUser botUser = new BotUser();
                botUser.setChatId(chatId.toString());
                botUser.setState(State.FULL_NAME);
                userRepository.save(botUser);
            }


            execute(sendMessage);

        }
    }
}
