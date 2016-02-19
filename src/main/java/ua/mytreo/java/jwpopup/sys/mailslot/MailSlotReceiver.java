package ua.mytreo.java.jwpopup.sys.mailslot;

import com.sun.jna.ptr.IntByReference;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 *  @author mytreo
 * @version 1.0
 * 17.02.2016.
 */
public class MailSlotReceiver {

    JNAKernel32 k32lib;
    int lastError = 0;
    int nextMsgSize = 390; //0
    String slotPath;

    public MailSlotReceiver() {
        k32lib = JNAKernel32.INSTANCE;
        slotPath = "messngr";
    }

    public void startReceiver() {

        System.out.println("\nStart msg Receiver");
        System.out.println("------------");

        if (!isMailSlotExists()) {
            //256
            int hSlot = createMailSlot( 0, JNAKernel32.MAILSLOT_WAIT_FOREVER);

            //передаем хендлер в отдельный поток

            if (hSlot > 0) {
                System.out.println("hSlot \\\\.\\mailslot\\" + slotPath + " = " + Integer.toString(hSlot));

                try {
                    while (!hasMessage(hSlot)) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Memory msg = new Memory(nextMsgSize);
                //while(true) {
                while (hasMessage(hSlot)) {
                    ByteBuffer msg = ByteBuffer.allocate(nextMsgSize);
                    IntByReference read = new IntByReference();

                    k32lib.ReadFile(hSlot, msg, nextMsgSize, read, 0);

                    //oemToChar
                    String getMes = new String(msg.array(), Charset.forName("cp866")); //
                    System.out.println(getMes);

                }
                //}
                k32lib.CloseHandle(hSlot);
            } else
                System.out.println("Create MailSlot failed.");
        } else
            System.out.println("Mailslot already exists");
    }

    public int createMailSlot(int maxSize, int timeOut) {
        String fullPath = "\\\\.\\mailslot\\" + slotPath;

        int hSlot = k32lib.CreateMailslot(fullPath, maxSize, timeOut, 0);
        if (hSlot == JNAKernel32.INVALID_HANDLE_VALUE)
            lastError = k32lib.GetLastError();

        return hSlot;
    }


    public boolean isMailSlotExists() {
        String fullPath = "\\\\.\\mailslot\\" + slotPath;
        int hFile = k32lib.CreateFile(fullPath, JNAKernel32.GENERIC_READ + JNAKernel32.GENERIC_WRITE, JNAKernel32.FILE_SHARE_READ, 0, JNAKernel32.OPEN_EXISTING, 0, 0);
        if (hFile != JNAKernel32.INVALID_HANDLE_VALUE) {
            k32lib.CloseHandle(hFile);
            return true;
        }
        return false;
    }

    public boolean hasMessage(int hSlot) {
        IntByReference maxMsg = new IntByReference();
        IntByReference nextMsg = new IntByReference();
        IntByReference msgCount = new IntByReference();
        IntByReference timeOut = new IntByReference();

        if (k32lib.GetMailslotInfo(hSlot, maxMsg, nextMsg, msgCount, timeOut)) {
            nextMsgSize = nextMsg.getValue();
            return msgCount.getValue() > 0;
        } else
            return false;
    }
}
