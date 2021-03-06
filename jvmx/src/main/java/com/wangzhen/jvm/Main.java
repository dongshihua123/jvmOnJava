package com.wangzhen.jvm;

import com.wangzhen.jvm.classfile.classPackage.ClassFile;
import com.wangzhen.jvm.classfile.classPath.ClassPath;
import com.wangzhen.jvm.instructions.Interpreter;
import com.wangzhen.jvm.runtimeData.ZFrame;
import com.wangzhen.jvm.runtimeData.ZThread;
import com.wangzhen.jvm.runtimeData.helap.ZClass;
import com.wangzhen.jvm.runtimeData.helap.ZClassLoader;
import com.wangzhen.jvm.runtimeData.helap.ZMethod;
import org.apache.commons.cli.*;

import java.util.Arrays;


/**
 *  使用说明：
 *  1.启动参数
 * -cp jvmx/target/classes/com/wangzhen/jvm  app
 */
public class Main {
    public static void main(String[] args) {
        // https://blog.csdn.net/yamaxifeng_132/article/details/87822812
        Options options = new Options();
        options.addOption("h", "help", false, "打印帮助信息");
        options.addOption("v", "version", false, "显示当前jdk 版本");
        options.addOption("jar",true,"执行一个jar");
        //options.addOption("cp","classpath",true,"指定类路径");
        Option cpOption = OptionBuilder
                .withArgName("args")
                .withLongOpt("classpath")
                .hasArgs()
                .withDescription("制定类路径")
                .create("cp");
        options.addOption(cpOption);

        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cli = parser.parse(options, args);
            if (cli.hasOption("h")){
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("Options", options);
                //System.out.println("这是jvmOnJava的帮助信息!");
            }
            if(cli.hasOption("v")){
                System.out.println("jdk1.8");
            }
            if(cli.hasOption("jar")){
                String []jarParameters = cli.getOptionValues("jar");
                System.out.println(Arrays.asList(jarParameters));
            }

            // java -jar jvm.jar -cp classes/com/wangzhen/jvm/ app.class
            // -cp cmd/target/classes/com/wangzhen/jvm app.class
            if(cli.hasOption("cp")){
                String []cpParameters = cli.getOptionValues("cp");
                DirEntry dirEntry = new DirEntry(cpParameters[0]);

//                byte [] classFileData = dirEntry.readClass(cpParameters[1]);
//                ClassFile classFile = new ClassFile(classFileData);
//                System.out.println(classFile.toString());
                startJvm(cpParameters);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void startJvm(String [] cpParameters) throws NoSuchMethodException {
        ClassPath classPath = new ClassPath("",cpParameters[0]);
        ZClassLoader classLoader = new ZClassLoader(classPath);
        ZClass zClass =  classLoader.loadClass(cpParameters[1]);
        ZMethod zMethod = zClass.getMainMethod();
        ZThread thread = new ZThread();
        ZFrame frame = new ZFrame(thread,zMethod);
        thread.pushFrame(frame);
        Interpreter.loop(thread);

    }



}
