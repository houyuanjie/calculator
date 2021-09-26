package icu.harx;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

public class CalculatorFrame extends JFrame {
    private final Context context;

    private final JTextField commandsTextField = new JTextField();
    private final JTextField resultTextField = new JTextField();

    private String result = "0";
    private String commands = "";

    public CalculatorFrame(Context context) {
        super("计算器");

        this.context = context;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        commandsTextField.setEditable(false);
        commandsTextField.setVisible(true);
        commandsTextField.setHorizontalAlignment(SwingConstants.RIGHT);

        final GridBagConstraints commandsTextFieldGBC = newGBC(0, 0);
        commandsTextFieldGBC.gridwidth = 4;
        mainPanel.add(commandsTextField, commandsTextFieldGBC);

        resultTextField.setEditable(false);
        resultTextField.setVisible(true);
        resultTextField.setHorizontalAlignment(SwingConstants.RIGHT);

        final GridBagConstraints resultTextFieldGBC = newGBC(0, 1);
        resultTextFieldGBC.gridwidth = 4;
        mainPanel.add(resultTextField, resultTextFieldGBC);

        final JButton b0 = new JButton("0");
        final JButton b1 = new JButton("1");
        final JButton b2 = new JButton("2");
        final JButton b3 = new JButton("3");
        final JButton b4 = new JButton("4");
        final JButton b5 = new JButton("5");
        final JButton b6 = new JButton("6");
        final JButton b7 = new JButton("7");
        final JButton b8 = new JButton("8");
        final JButton b9 = new JButton("9");

        final JButton bClear = new JButton("C");
        final JButton bDot = new JButton(".");
        final JButton bPlus = new JButton("+");
        final JButton bMinus = new JButton("-");
        final JButton bTimes = new JButton("*");
        final JButton bDividedBy = new JButton("/");
        final JButton bXor = new JButton("^");
        final JButton bExp = new JButton("e");
        final JButton bMod = new JButton("%");
        final JButton bSqrt = new JButton("√");
        final JButton bLBrace = new JButton("(");
        final JButton bRBrace = new JButton(")");
        final JButton bBack = new JButton("<");
        final JButton bEquals = new JButton("=");

        mainPanel.add(bClear, newGBC(0, 2));
        mainPanel.add(bDividedBy, newGBC(1, 2));
        mainPanel.add(bTimes, newGBC(2, 2));
        mainPanel.add(bBack, newGBC(3, 2));
        mainPanel.add(bXor, newGBC(0, 3));
        mainPanel.add(bExp, newGBC(1, 3));
        mainPanel.add(bMod, newGBC(2, 3));
        mainPanel.add(bSqrt, newGBC(3, 3));
        mainPanel.add(b7, newGBC(0, 4));
        mainPanel.add(b8, newGBC(1, 4));
        mainPanel.add(b9, newGBC(2, 4));
        mainPanel.add(bMinus, newGBC(3, 4));
        mainPanel.add(b4, newGBC(0, 5));
        mainPanel.add(b5, newGBC(1, 5));
        mainPanel.add(b6, newGBC(2, 5));
        mainPanel.add(bPlus, newGBC(3, 5));
        mainPanel.add(b1, newGBC(0, 6));
        mainPanel.add(b2, newGBC(1, 6));
        mainPanel.add(b3, newGBC(2, 6));
        mainPanel.add(bDot, newGBC(3, 6));
        mainPanel.add(b0, newGBC(0, 7));
        mainPanel.add(bLBrace, newGBC(1, 7));
        mainPanel.add(bRBrace, newGBC(2, 7));
        mainPanel.add(bEquals, newGBC(3, 7));

        // 清除按钮响应
        bClear.addActionListener(event -> {
            this.commands = "";
            this.result = "0";

            this.updateView();
        });

        // 退格按钮响应
        bBack.addActionListener(event -> {
            if (this.commands.length() > 0) {
                this.commands = this.commands.substring(0, this.commands.length() - 1);
            } else {
                this.commands = "";
                this.result = "0";
            }

            this.updateView();
        });

        // 输入按钮响应
        Stream.of(b0, b1, b2, b3, b4, b5, b6, b7, b8, b9,
                        bDot, bPlus, bMinus, bLBrace, bRBrace,
                        bTimes, bDividedBy, bXor, bExp, bMod)
                .forEach(button -> button.addActionListener(
                        event -> {
                            this.commands += event.getActionCommand();

                            this.updateView();
                        }
                ));

        // sqrt 按钮响应
        bSqrt.addActionListener(event -> {
            this.commands = "√" + this.result;

            try {
                this.result = this.context.eval("js", "Math.sqrt(" + this.result + ")").toString();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            this.updateView();
        });

        // 求值按钮响应
        bEquals.addActionListener(event -> {
            try {
                final String value = this.context.eval("js", this.commands).toString();

                this.result = switch (value) {
                    case "Infinity" -> "无穷, 请检查输入";
                    case "-Infinity" -> "负无穷, 请检查输入";
                    case "undefined" -> "未知输入";
                    default -> value;
                };
            } catch (PolyglotException polyglotException) {
                polyglotException.printStackTrace();

                if (polyglotException.isSyntaxError()) {
                    this.result = "语法错误";
                } else if (polyglotException.isGuestException()) {
                    this.result = "表达式错误";
                } else {
                    this.result = polyglotException.getMessage();
                }
            }

            this.updateView();
        });

        // 窗口参数
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setSize(220, 280);
        this.setResizable(false);
        this.setLocationByPlatform(true);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        final Context context = Context.newBuilder("js").build();
        final CalculatorFrame calculatorFrame = new CalculatorFrame(context);

        calculatorFrame.updateView();
    }

    private static GridBagConstraints newGBC(int gridX, int gridY) {
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1D;
        gridBagConstraints.weighty = 1D;

        gridBagConstraints.gridx = gridX;
        gridBagConstraints.gridy = gridY;

        return gridBagConstraints;
    }

    // 视图刷新(数据绑定)
    private void updateView() {
        this.commandsTextField.setText(this.commands);
        this.resultTextField.setText(this.result);
    }
}
