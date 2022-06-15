package uz.pdp.official_support_company_bot.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pdp.official_support_company_bot.entity.BotUser;
import uz.pdp.official_support_company_bot.entity.Messages;
import uz.pdp.official_support_company_bot.repository.BotUserRepository;
import uz.pdp.official_support_company_bot.service.BotService;

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
            sendMessage.setText(" ");

            Optional<BotUser> optionalBotUser = userRepository.findByChatId(chatId.toString());

            if (optionalBotUser.isPresent()) {
                current_user = optionalBotUser.get();

                String state = current_user.getState();

                if (message.getText() != null && message.getText().equals("/start")) {
                    current_user.setState(State.START);
                    userRepository.save(current_user);
                }


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
                            sendMessage = botService.askingTheirPosition(update, current_user);
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
                        current_user.setState(State.ENTERING_CODE);
                        userRepository.save(current_user);
                        break;
                    case State.ENTERING_CODE:
                        sendMessage = botService.user_or_admin(update, current_user,code);
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


                    case State.COMMENTING:
                        // comment yuboriladi HR ga
                        sendMessage = botService.commenting(update, current_user);
                        current_user.setState(State.USER_OR_ADMIN);
                        userRepository.save(current_user);
                        break;

                    case State.SETTING_WEEKLY_TARGET:
                        sendMessage = botService.setWeeklyTarget(update, current_user);
                        break;

                    case State.SENDING_NEWS:
                        String messageText = message.getText();

                        Messages messages = new Messages();
                        messages.setReceiver(current_user);;
                        messages.setText(messageText);


                        current_user.setState(State.SEND_TO_ONE);
                        userRepository.save(current_user);

                        List<BotUser> all = userRepository.findAll();

                        // hamm userga xabar jo'natilayapti

                        for (BotUser botUser : all) {
                            sendMessage.setChatId(botUser.getChatId());
                            execute(sendMessage);
                        }
                        sendMessage.setText("your message is sent to All users!");
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + state);
                }

            } else {
                if (text.equals("/start")) {
                    sendMessage = botService.welcome(update);
                }
                BotUser botUser = new BotUser();
                botUser.setChatId(chatId.toString());
                botUser.setState(State.FULL_NAME);
                BotUser save = userRepository.save(botUser);
            }

            execute(sendMessage);

        }
    }
}
