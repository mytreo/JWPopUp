package ua.mytreo.java.sys.mailslot;

import com.sun.jna.ptr.IntByReference;
import ua.mytreo.java.App;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 *  @author mytreo
 * @version 1.0
 * 17.02.2016.
 */
public class MailSlotSender {
    JNAKernel32 k32lib;
    String slotPath;

    public MailSlotSender() {
        k32lib = JNAKernel32.INSTANCE;
        slotPath = "messngr";
    }

    public void sendMessageTo(String to, String textMessage) {
        char a = '\0'; //EndOfString NULL
        char b = '\13'; //enter CR
        char c = '\10'; //NewString LF
        boolean result = false;
        String fullPath = "\\\\" + to + "\\mailslot\\" + slotPath;
        //  '\0' = #0
        // msgFormat =  "Sender+#0+Reciever+#0^ ololo message"+ +#13#10 podpis +#13#10 deviz";

        String prefix = App.getComputerName() + a + to + a;
        /*String msg = "123456789 123456789 123456789 123456789 " +
				     "123456789 123456789 123456789 123456789 " +
				     "123456789 123456789 123456789 123456789 " +
				     "12456789"
			     	 +"";*/

        String msg = prefix  + "^"+"english 123456789 русская";
        IntByReference written = new IntByReference();

        System.out.println("ClientTest");
        System.out.println("----------");

        System.out.println("Try to send message " + msg + " into " + fullPath);
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
