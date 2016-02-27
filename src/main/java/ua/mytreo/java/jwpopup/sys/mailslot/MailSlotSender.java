package ua.mytreo.java.jwpopup.sys.mailslot;

import com.sun.jna.ptr.IntByReference;
import ua.mytreo.java.jwpopup.App;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author mytreo
 * @version 1.0
 *          17.02.2016.
 */
public class MailSlotSender {
    JNAKernel32 k32lib;
    String slotPath;
    final int maxLengthWithPrefix = 390;

    public MailSlotSender() {
        k32lib = JNAKernel32.INSTANCE;
        slotPath = "messngr";
    }

    public void sendMessageTo(String to, String textMessage) {
        char a = '\0'; //EndOfString NULL
        char b = '\13'; //enter CR
        char c = '\10'; //NewString LF

        String fullPath = "\\\\" + to + "\\mailslot\\" + slotPath;
        //  '\0' = #0
        // msgFormat =  "Sender+#0+Reciever+#0^ ololo message"+ +#13#10 podpis +#13#10 deviz"
        String prefix = App.getComputerName() + a + to + a;
        Integer msgLenth = maxLengthWithPrefix - prefix.length();

        String[] messages;
        if (textMessage.length() > msgLenth) {
            //дробим не регуляркой
            messages = messageSplit(textMessage,msgLenth);

            for (int j = 0; j < messages.length - 1; j++) {
                messages[j] = ("%%" + messages[j]);
            }
            messages[messages.length - 1] = ("^@@" + messages[messages.length - 1]);
        } else {
            messages = new String[1];
            messages[0] = "^" + textMessage;
        }

        //send messages in another thread
        new Thread(new SenderHelper(messages,prefix, fullPath)).start();

    }

    private class SenderHelper implements Runnable{
        private String[] messages;
        private String prefix;
        private String fullPath;
        private boolean result = false;

        SenderHelper(String[] messages,String prefix,String fullPath){
            this.messages=messages;
            this.prefix=prefix;
            this.fullPath=fullPath;

        }
        @Override
        public void run() {
            for (String message : messages) {
                String msg = prefix + message;
                System.out.println("Try to send message " + msg + " into " + fullPath);

                IntByReference written = new IntByReference();
                int hFile = k32lib.CreateFile(fullPath, JNAKernel32.GENERIC_WRITE, JNAKernel32.FILE_SHARE_READ, 0, JNAKernel32.OPEN_EXISTING, JNAKernel32.FILE_ATTRIBUTE_NORMAL, 0);
                try {
                    if (hFile != JNAKernel32.INVALID_HANDLE_VALUE) {

                        ByteBuffer bb = Charset.forName("cp866").encode(msg);
                        k32lib.WriteFile(hFile, bb, bb.array().length, written, 0);
                        if (!((msg.length()) == written.getValue())) {
                            throw new Exception("something with msg length");
                        }

                        System.out.println("Msg dostavko " + Integer.toString(written.getValue()) + " octets");
                        result = true;
                    } else {
                        throw new Exception("ohShit invalid slot");
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    k32lib.CloseHandle(hFile);
                }

                if (result) {
                    System.out.println("Otpravleno");
                } else {
                    System.out.println("Ne otpravleno");
                }
            }
        }
    }

    private String[] messageSplit(String textMessage, int maxLength) {
        int kol = textMessage.length() / maxLength;
        String[] newStrings = new String[kol];
        for (int i = 0; i < kol; i++) {
            newStrings[i] = textMessage.substring(i + maxLength * i - (1 * (i == 0 ? 0 : 1)), i + maxLength + maxLength * i);
        }
        return newStrings;
    }
}
