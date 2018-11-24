package com.greatgaming.client.ui;

import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.RunState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwinglUI extends GameUI {
    public static void main(String[] args){
        SwinglUI ui = new SwinglUI();
        ui.run();
    }
    private DefaultListModel listModel = new DefaultListModel();

    @Override
    public synchronized void addGameStateChange(GameState gameState) {
        if (gameState instanceof ChatState) {
            java.util.List<String> messages = ((ChatState)gameState).getPendingChatLogChanges();
            for (String message : messages) {
                listModel.add(0, message);
            }
        }
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 370));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,
                BoxLayout.Y_AXIS));

        JList list = new JList(listModel);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setAlignmentX(0);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(400, 370));

        JTextField myField = new JTextField();
        myField.setPreferredSize(new Dimension(400, 30));
        myField.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0),
                "takeInput");
        myField.getActionMap().put("takeInput", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String myVal = myField.getText();
                myField.setText("");

                ChatState chatState = new ChatState();
                chatState.addToChatLog(myVal);
                outgoingGameStateChanges.add(chatState);
            }
        });

        panel.add(myField);
        panel.add(scrollPane);

        frame.getContentPane().add(panel);

        frame.pack();

        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                RunState runState = new RunState();
                runState.shutDownGame();
                outgoingGameStateChanges.add(runState);
                keepAlive = false;
            }
        });
    }
}
