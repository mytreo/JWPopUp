package ua.mytreo.java.jwpopup.sys.mailslot;

import com.sun.jna.ptr.IntByReference;
import ua.mytreo.java.jwpopup.dbservice.DBException;
import ua.mytreo.java.jwpopup.dbservice.DBService;
import ua.mytreo.java.jwpopup.dbservice.dataSets.MessagesDataSet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mytreo
 * @version 1.0
 *          17.02.2016.
 */
public class MailSlotReceiver extends Thread {

    DBService dbService = new DBService();
    JNAKernel32 k32lib;
    int lastError = 0;
    int nextMsgSize = 390; //0
    String slotPath;
    int hSlot;

    public MailSlotReceiver() {
        k32lib = JNAKernel32.INSTANCE;
        slotPath = "messngr";
    }

    public void startReceiver() {

        System.out.println("\nStart msg Receiver");
        System.out.println("------------");

        if (!isMailSlotExists()) {
            //256
            hSlot = createMailSlot(0, JNAKernel32.MAILSLOT_WAIT_FOREVER);

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
                while (true) {
                    while (hasMessage(hSlot)) {
                        ByteBuffer msg = ByteBuffer.allocate(nextMsgSize);
                        IntByReference read = new IntByReference();
                        Map<String,String> unfinishedMessages = new HashMap<>();

                        k32lib.ReadFile(hSlot, msg, nextMsgSize, read, 0);

                        //oemToChar
                        String getMes = new String(msg.array(), Charset.forName("cp866")); //

                       ParsedMessage pm = new ParsedMessage(getMes);

                        switch (pm.type){
                            case 1:{
                                if(unfinishedMessages.containsKey(pm.from)){
                                    unfinishedMessages.replace(pm.from,unfinishedMessages.get(pm.from)+pm.text);
                                }else{
                                    unfinishedMessages.put(pm.from,pm.text);
                                }
                                break;
                            }
                            case 2:{
                                unfinishedMessages.replace(pm.from,unfinishedMessages.get(pm.from)+pm.text);
                                messageToUser(pm.from,unfinishedMessages.get(pm.from));
                                unfinishedMessages.remove(pm.from);
                                break;
                            }
                            case 3:{
                                messageToUser(pm.from,pm.text);
                                break;
                            }
                            case 4:{
                                setMessageReceived(pm.from);
                                break;
                            }
                        }

                    }
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        System.out.println("interrupted");
                    }
                }

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

    @Override
    public void run() {
        startReceiver();
        k32lib.CloseHandle(hSlot);
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

    private void messageToUser(String from,String text){
        int idContact;
        try {
            idContact = dbService.getContactIdByAdress(from);
            dbService.addMessage(new MessagesDataSet(0, text, idContact, 0, 0, (new Date()).getTime()));
        } catch (DBException e) {
            e.printStackTrace();
        }

        //todo refresh list if dialog active
    }

    private void setMessageReceived(String from){
        try {
            dbService.setSuccessOnLastSendedMessageToContact(from);
        } catch (DBException e) {
            e.printStackTrace();
        }
        //todo otmetko dostavko
    }


    private class ParsedMessage{
        String from;
        int type;
        String text;

        ParsedMessage(String receivedMessageText){
            String[] messageParts = receivedMessageText.split("\0");
            from =  messageParts[0];

            String messageText = "";
            if (messageParts.length > 3) {
                for (int i = 2; i < messageParts.length; i++) {
                    messageText = messageText + "\0" + messageParts[i];
                }
            } else {
                messageText = messageParts[2];
            }

            if (messageText.startsWith("%%")) {
                type = 1;
                text = messageText.substring(2);
            } else if (messageText.startsWith("^@@")) {
                type = 2;
                text = messageText.substring(3);
            } else if (messageText.startsWith("^")) {
                type = 3;
                text = messageText.substring(1);
            } else { //##USER-
                type = 4;
                text = messageText.substring(7);
            }
        }
    }

}
