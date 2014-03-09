package k.core.util.github;

import java.io.*;

import k.core.util.netty.DataStruct;

/**
 * Handles all storage, NOTHING gives this data, it pulls all. IT COMMANDS ALL.
 * and it gets one chance to do it.
 * 
 * @author Kenzie Togami
 *
 */
final class GStore {
    static void storeGitData() {
        DataStruct dataStruct = new DataStruct();
        dataStruct.add(GNet.authorization);
        File config = new File("./config/config.datastruct").getAbsoluteFile();
        try {
            config.getParentFile().mkdirs();
            config.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("path: " + config, e);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(config);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try {
            fos.write(dataStruct.toString().getBytes());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                // ignore closing problems
            }
        }
    }
}
