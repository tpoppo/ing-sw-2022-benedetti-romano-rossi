package it.polimi.ingsw.utils;

import java.io.*;

/**
 * Helper class used to copy an object.
 * Mainly used simulate a message without sending it.
 */
public class DeepCopy {

    public static Object copy(Serializable object){
        //Serialization of object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);

            //De-serialization of object
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e); // it is unreachable
        }
    }
}
