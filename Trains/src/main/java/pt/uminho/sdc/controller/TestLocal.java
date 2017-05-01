package pt.uminho.sdc.controller;

public class TestLocal {
    public static void main(String[] args) throws Exception {
        Controller b = new ControllerImpl();
        new Tester(() -> b, args).test();
    }
}
