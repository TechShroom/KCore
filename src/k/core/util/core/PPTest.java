package k.core.util.core;

import java.util.Arrays;

import k.core.util.core.Helper.ProgramProps;

public class PPTest {

    public static void main(String[] args) {
        System.err.println(Arrays.toString(args));
        args = ProgramProps.normalizeCommandArgs(args);
        ProgramProps.acceptAll(args);
        System.err.println(Arrays.toString(args));
        System.err.println(Arrays.toString(Helper.BetterArrays.splice(args, 0,
                args.length, -1)));
    }

}
