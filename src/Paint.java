import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/** A program that allows the user to paint via a brush with color customization.
 * */
public class Paint extends JFrame {
    public static final Color TOP_SIDE_COLOR = new Color(0xB8D7FA);

    public static final String[] MANUAL_TITLES = new String[]{
            "load button function",
            "save button function",
            "reset button function",
            "stroke slider function",
            "options menu",
            "color settings",
            "color settings",
            "color settings",
            "color settings",
            "color settings"
    };

    public static final String[] MANUAL_MSG = new String[]{
            "the 'load' button at the top bar allows you to open\na series of images and files to load them on screen.",
            "the 'save' button allows you to open a files to save\nthe image.",
            "the 'reset' button allows you to remove all drawings\npre-made on the canvas.",
            "the topmost slider at the options panel to the left\ncan adjust brush/erase size",
            "below the stroke slider are the options panel\n- pen draws with a specific color\n- eraser erases\n- bucket fills the canvas with a specific color\n- pipette takes the color on screen and saves it\n- spatula blends colors together\n- text writer allows you to write text on screen",
            "below the options panel is a series of sliders that\nallows you to adjust color",
            "you can choose a total of 10 pre-selected colors",
            "or you may use the slider to choose a custom color\nwith a custom quantity of red, green, or blue",
            "a panel is right below the color sliders indicating\nwhat color you have chosen",
            "and finally, you can use the text panel below to\ndetermine what specific color it is or of course you can put\nin your own pre-chosen color using hex(#000000) or\nrgb(r:0-255, g:0-255, b:0-255) format"
    };

    /** Main constructor for the program, defines the properties of the frame of this particular paint editor.
     * */
    public Paint() {
        super("paint editor");
        setPreferredSize(new Dimension(1920, 1080));
        setVisible(true);
        setResizable(false);
        getContentPane().add(createMenu(), BorderLayout.NORTH);
        getContentPane().add(new Canvas(), BorderLayout.CENTER);
        getContentPane().add(new OptionPanel(), BorderLayout.EAST);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            /* Allows the program to ask the user whether to save their drawing prior to exiting.
             * First by checking whether there is a canvas then checking if it has a drawing on it before the asking the user.
             * */
            @Override
            public void windowClosing(WindowEvent e) {
                if (getContentPane().getComponent(1) instanceof Canvas canvas && canvas.hasDrawings()) {
                    if (JOptionPane.showConfirmDialog(null, "Would you like to save your drawing before exiting?", "save?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        canvas.saveImage();
                    }
                }
                dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    /** Creates the top bar menu that allows for limited file manipulation.
     * A series of {@link MenuItem}s are created which is then added to a {@link MenuBar}.
     * @return panel with a menu bar containing menu items.
     * */
    private JPanel createMenu() {
        JPanel panel = new JPanel();
        panel.setBackground(TOP_SIDE_COLOR);

        JMenuBar bar = new JMenuBar();
        bar.add(menuItemCreator("load", e -> loadFile()));
        bar.add(menuItemCreator("save", e -> saveImage()));
        bar.add(menuItemCreator("reset", e -> reset()));
        bar.add(menuItemCreator("credit", e -> JOptionPane.showMessageDialog(null,
                "Program made by Bowen Leng", "Credit", JOptionPane.INFORMATION_MESSAGE)));
        bar.add(menuItemCreator("user manual", e -> userManual()));
        panel.add(bar, BorderLayout.LINE_START);

        return panel;
    }

    /**General program helper method that creates {@link MenuItem}s to be added to a {@link MenuBar}.
     * @param name name of the button, ie what is displayed on the button.
     * @param listener a lambda that defines what a button does when pressed.
     * @return a basic menu item with a displayed name and a defined function.
     * */
    private JMenuItem menuItemCreator(String name, ActionListener listener) {
        JMenuItem item = new JMenuItem(name);
        item.setBackground(TOP_SIDE_COLOR);
        item.addActionListener(listener);
        return item;
    }

    /** Checks whether component one is a canvas and invokes the canvas {@code loadFile()} function if true.
     * */
    private void loadFile() {
        if (getContentPane().getComponent(1) instanceof Canvas canvas) {
            canvas.loadFile();
        }
    }

    /** Checks whether component one is a canvas and invokes the canvas {@code saveFile()} function if true.
     * */
    private void saveImage() {
        if (getContentPane().getComponent(1) instanceof Canvas canvas) {
            canvas.saveImage();
        }
    }

    /** Checks whether component one is a canvas and invokes the canvas {@code reset()} function is true.
     * */
    private void reset() {
        if (getContentPane().getComponent(1) instanceof Canvas canvas) {
            canvas.reset();
        }
    }

    /** A method that uses {@link JOptionPane} to display a series of messages intended to guide the user how to use the program.
     * A while loop is first inputted to allow the user to exit out of the guide at any time.
     * */
    private void userManual() {
        int ind = 0;
        int statement = JOptionPane.OK_OPTION;
        while (statement == JOptionPane.OK_OPTION && ind < MANUAL_MSG.length && ind < MANUAL_TITLES.length ) {
            statement = JOptionPane.showConfirmDialog(null, MANUAL_MSG[ind], MANUAL_TITLES[ind], JOptionPane.OK_CANCEL_OPTION);
            ind++;
        }
    }

    /** @return a random color value.
     * */
    public static Color randomColor() {
        Random random = new Random();
        return new Color(random.nextInt(0xFFFFFF));
    }

    /** A function that reads the input string's indices to convert a hexadecimal string to a decimal value.
     * @param hex hexadecimal string.
     * @return the hexadecimal value converted into base 10.
    * */
    public static int hexToDec(String hex) {
        int decVal = 0;
        int multiplier = 1;
        for (int i = hex.length() - 1; i >= 0; i--) {
            char c = hex.charAt(i);
            int valToAdd = 0;
            if (c >= '0' && c <= '9') {
                valToAdd = c - 48;
            } else if (c >= 'A' && c <= 'F') {
                valToAdd = c - 55;
            }
            decVal += valToAdd * multiplier;
            multiplier *= 16;
        }
        return decVal;
    }

    /** A function that reads the input string's indices to convert the string into a proper number.
     * @param val the "number" as a string.
     * @return an integer read from val.
     * */
    public static int textAsInt(String val) {
        int num = 0;
        int multiplier = 1;
        for (int i = val.length() - 1; i >= 0; i--) {
            char c = val.charAt(i);
            if (c >= '0' && c <= '9') {
                num += (c - 48) * multiplier;
            }
            multiplier *= 10;
        }
        return num;
    }

    /** Converts color into a much shorter rgb string.
     * */
    public static String colorText(Color color) {
        return "(r:%s, g:%s, b:%s)".formatted(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static int compareColor(Color color1, Color color2) {
        if (color1.getRed() == color2.getRed()) {
            if (color1.getGreen() == color2.getGreen()) {
                if (color1.getBlue() == color2.getBlue()) {
                    return 0;
                }
                return convertToOne(color1.getBlue() - color2.getBlue());
            }
            return convertToOne(color1.getGreen() - color2.getGreen());
        }
        return convertToOne(color1.getRed() - color2.getRed());
    }

    private static int convertToOne(int num) {
        return Integer.compare(num, 0);
    }

    private static void colorSort(List<Color> colors) {
        colorSort(colors, 0, colors.size());
    }

    private static void colorSort(List<Color> colors, int beg, int end) {
        int mid = beg + (end - beg) / 2;
        if (mid != beg) {
            colorSort(colors, beg, mid);
            colorSort(colors, mid, end);
            colorMerge(colors, beg, end);
        }
    }

    private static void colorMerge(List<Color> colors, int beg, int end) {
        List<Color> temp = new ArrayList<>();
        int mid = beg + (end - beg) / 2;

        int start = beg;
        int cent = mid;

        while (start < mid && cent < end) {
            if (compareColor(colors.get(cent), colors.get(mid + cent)) < 0) {
                temp.add(colors.get(cent++));
            } else {
                temp.add(colors.get(start++));
            }
        }
        while (start < mid) temp.add(colors.get(start++));
        while (cent < end) temp.add(colors.get(cent++));
        for (int i = 0; i < temp.size(); i++) {
            colors.set(start + i, colors.get(i));
        }
    }

    public static void main(String[] args) {
        new Paint();
    }

    /** Class defining the option sidebar of the panel.
     * */
    static class OptionPanel extends JPanel {
        private static int SETTINGS = 0; // the user's mode for drawing.
        private static final JPanel PANEL = new JPanel(); // a panel that allows the user to visually see what exact color they've chosen.
        private static final JSlider STROKE_SLIDER = makeStrokeSlider(); // slider that allows the user to change the size of their pen/eraser/spatula.
        private static final JLabel STROKE_LABEL = labelMaker("stroke size: 5px"); // a label that displays the user's chosen stroke size.

        /** Color sliders that allows the user to customize the color of their brush and visualize how much color is in their chosen color.
         * */
        private static final JSlider RED_SLIDER = makeColorSlider("redness");
        private static final JSlider GREEN_SLIDER = makeColorSlider("greenness");
        private static final JSlider BLUE_SLIDER = makeColorSlider("blueness");

        // Label for the mouse's location.
        private static final JLabel LOC_LABEL = labelMaker("Mouse Location: (x:0, y:0)");

        // Label used to tell the user what color the brush is in along with allowing them to define their own customized color.
        private static final JTextField COLOR_TEXT = makeColorTextField();

        /** Regex fields used to help filter out whether the user's color input is valid or not.
         * */
        private static final String RGB_REGEX = "\\(?R?:?(2[0-4][0-9]|25[0-5]|[0-1]?([0-9]{1,2})),\\s*G?:?(2[0-4][0-9]|25[0-5]|[0-1]?([0-9]{1,2})),\\s*B?:?(2[0-4][0-9]|25[0-5]|[0-1]?([0-9]{1,2}))\\)?";
        private static final String HEX6_REGEX = "#?([0-9A-F]{6})";
        private static final String HEX3_REGEX = "#?([0-9A-F]{3})";


        /** Buttons used to define what mode the user is in when pressing on the canvas.
         * */
        private final JRadioButton pen = makeToolButton("pen", Canvas.DRAW);
        private final JRadioButton eraser = makeToolButton("eraser", Canvas.ERASE);
        private final JRadioButton bucket = makeToolButton("bucket", Canvas.FILL);
        private final JRadioButton pipette = makeToolButton("pipette", Canvas.COPY_COLOR);
        private final JRadioButton spatula = makeToolButton("spatula", Canvas.MIX);
        private final JRadioButton textWriter = makeToolButton("text writer", Canvas.WRITE);
        private final JRadioButton colorSet = makeToolButton("color set", Canvas.SET);

        /** A basic constructor that creates a sidebar for the main frame that allows for customization of the brush.
         * */
        public OptionPanel() {
            super();
            setBackground(Color.LIGHT_GRAY);
            setPreferredSize(new Dimension(200, 1080));
            add(STROKE_LABEL);
            add(STROKE_SLIDER);
            add(labelMaker("options"));
            createToolSelection();
            add(makeColorChooser());
            add(labelMaker("redness"));
            add(RED_SLIDER);
            add(labelMaker("greenness"));
            add(GREEN_SLIDER);
            add(labelMaker("blueness"));
            add(BLUE_SLIDER);
            add(COLOR_TEXT);
            PANEL.setBackground(Color.BLACK);
            PANEL.setPreferredSize(new Dimension(50, 50));
            add(PANEL);
            add(COLOR_TEXT);

            add(LOC_LABEL, BorderLayout.SOUTH);
        }

        /** Make an internal panel within the sidebar panel that allows
         * the user to choose between 10 separate, individual colors.
         * @return the panel the default set of colors are on.
         * */
        private JPanel makeColorChooser() {
            JPanel panel = new JPanel();
            panel.setBackground(Color.LIGHT_GRAY);
            panel.setPreferredSize(new Dimension(200, 50));

            panel.add(createColorButton(Color.WHITE));
            panel.add(createColorButton(Color.GRAY));
            panel.add(createColorButton(Color.BLACK));
            panel.add(createColorButton(Color.RED));
            panel.add(createColorButton(Color.ORANGE));
            panel.add(createColorButton(Color.YELLOW));
            panel.add(createColorButton(Color.GREEN));
            panel.add(createColorButton(Color.CYAN));
            panel.add(createColorButton(Color.BLUE));
            panel.add(createColorButton(Color.MAGENTA));
            return panel;
        }

        /** Helper method for {@code makeColorChooser()} that aids in creating buttons quickly.
         * @param color the color of the button in the background and also the color the button sets the painter to.
         * @return the button with all the properly configured states.
         * */
        private JButton createColorButton(Color color) {
            JButton button = new JButton();
            button.setBackground(color);
            button.addActionListener(e -> setColorSlider(color));
            return button;
        }


        /** Groups multiple buttons together for a well formatted system of selection for the user.
         * Done using {@link ButtonGroup} groups to add all individual buttons while also adding them
         * to the panel as components themselves.
         * */
        private void createToolSelection() {
            ButtonGroup group = new ButtonGroup();
            group.add(pen);
            group.add(eraser);
            group.add(bucket);
            group.add(pipette);
            group.add(spatula);
            group.add(textWriter);
            group.add(colorSet);

            add(pen);
            add(eraser);
            add(bucket);
            add(pipette);
            add(spatula);
            add(textWriter);
            add(colorSet);
            group.setSelected(pen.getModel(), true);
        }

        /** Makes a tool button that changes the value of {@code SETTINGS} in order to allow for more customization on drawing.
         * @param name Name of the button, set as what's displayed to the user.
         * @param value The value the button sets {@code SETTINGS} to.
         * @return A properly formatted button.
         * */
        private JRadioButton makeToolButton(String name, final int value) {
            JRadioButton button = new JRadioButton(name);
            button.setBackground(Color.LIGHT_GRAY);
            button.addActionListener(e -> SETTINGS = value);
            button.setPreferredSize(new Dimension(200, 20));
            return button;
        }

        /** Produces a {@link JTextField} that can be modified to allow for specification of colors on screen.
         * All the functions of the key listener does the same thing in which when the exit key is pressed,
         * the color is inputted through the program to put out a viable color if the text is a color.
         * @return a text field with formatting and a key listener.
         * */
        private static JTextField makeColorTextField() {
            JTextField textField = new JTextField(colorText(Color.BLACK));
            textField.setPreferredSize(new Dimension(150, 20));
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        textToColor(textField.getText());
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        textToColor(textField.getText());
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        textToColor(textField.getText());
                    }
                }
            });
            return textField;
        }

        /** A function that converts a piece of text into a color and saves it to the slider.
         * The conversion of the text is done either by the name of the color with correct spelling or
         * whether it's rgb or hex color code.
         * @param input the text being converted to color.
         * */
        private static void textToColor(String input) {
            input = input.toUpperCase();
            if (colorInputIsValid(input)) {
                Color color = switch (input) {
                    case "WHITE" -> Color.WHITE;
                    case "LIGHT GRAY" -> Color.LIGHT_GRAY;
                    case "GRAY" -> Color.GRAY;
                    case "BLACK" -> Color.BLACK;
                    case "RED" -> Color.RED;
                    case "PINK" -> Color.PINK;
                    case "ORANGE" -> Color.ORANGE;
                    case "YELLOW" -> Color.YELLOW;
                    case "GREEN" -> Color.GREEN;
                    case "MAGENTA" -> Color.MAGENTA;
                    case "CYAN" -> Color.CYAN;
                    case "BLUE" -> Color.BLUE;
                    case "RANDOM" -> randomColor();
                    default -> convertStrToColor(input);
                };
                setColorSlider(color);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Color: %s".formatted(input), "Invalid Color!", JOptionPane.ERROR_MESSAGE);
                COLOR_TEXT.setText(colorText(getSliderColor()));
            }
        }

        /** A method that uses regex to determine whether the input is rgb or hex and dissects the numbers from the string
         * to change the values of the color sliders.
         * @param input the color string being red.
         * @return the color of {@code input} or the color black if {@code input} does not match any of the regex.
         * */
        public static Color convertStrToColor(String input) {
            if (input.matches(RGB_REGEX)) {
                String[] colors = input.trim().replaceAll("[^,\\d]", "").split(",");
                String r = colors[0];
                String g = colors[1];
                String b = colors[2];
                int red = textAsInt(r);
                int green = textAsInt(g);
                int blue = textAsInt(b);
                return new Color(red, green, blue);
            } else if (input.matches(HEX6_REGEX)) {
                return new Color(hexToDec(input.replaceAll("[\\WG-Z]", "")));
            } else if (input.matches(HEX3_REGEX)) {
                int decVal = hexToDec(input.replaceAll("[\\WG-Z]", ""));
                int red = (decVal / 256) % 16;
                int green = (decVal / 16) % 16;
                int blue = decVal % 16;
                return new Color(red * 16, green * 16, blue * 16);
            }
            return Color.BLACK;
        }

        /** @param input the string "color", should be uppercase.
         * @return whether a string is not null along with whether it either matches with a color name or a regex value.
         * */
        public static boolean colorInputIsValid(String input) {
            return input != null && (isColorName(input) || input.matches(HEX6_REGEX) ||
                    input.matches(RGB_REGEX) || input.matches(HEX3_REGEX));
        }

        /** @param input the "color name", should be uppercase.
         * @return whether {@code input} matches with a proper color name.
         * */
        public static boolean isColorName(String input) {
            return input.equals("WHITE") || input.equals("LIGHT GRAY") || input.equals("GRAY") || input.equals("BLACK")
                    || input.equals("RED") || input.equals("PINK") || input.equals("ORANGE") || input.equals("YELLOW")
                    || input.equals("GREEN") || input.equals("MAGENTA") || input.equals("CYAN") || input.equals("BLUE")
                    || input.equals("RANDOM");
        }

        /** Simple label maker that formats all labels as to have the same background as the sidebar along with proper spacing.
         * @param name What is being displayed on the label.
         * @return properly formatted label.
         * */
        private static JLabel labelMaker(String name) {
            JLabel label = new JLabel(name);
            label.setSize(new Dimension(200, 40));
            label.setBackground(Color.LIGHT_GRAY);
            return label;
        }

        /** Method defining {@code STROKE_LABEL} that creates a slider that can be read by
         * {@link Paint.Canvas#paint(MouseEvent)} to change the size of the pen or eraser.
         * @return A formatted stroke slider.
         * */
        private static JSlider makeStrokeSlider() {
            JSlider slider = new JSlider();
            slider.setToolTipText("stroke size");
            slider.setMinimum(5);
            slider.setValue(5);
            slider.setBackground(Color.LIGHT_GRAY);
            slider.addChangeListener(e -> STROKE_LABEL.setText("stroke size: %spx".formatted(slider.getValue())));
            return slider;
        }

        /** Method that creates color sliders that creates a slider that changes the color of what is being drawn in the program
         * @param name the hover text of the slider.
         * @return a formatted slider with a change listener.
         * */
        private static JSlider makeColorSlider(String name) {
            JSlider slider = new JSlider();
            slider.setToolTipText(name);
            slider.setMaximum(255);
            slider.setMinimum(0);
            slider.setValue(0);
            slider.setBackground(Color.LIGHT_GRAY);
            slider.addChangeListener(e -> colorSliderAttribute());
            return slider;
        }

        /** Helper method for {@code makeColorSlider} that changes the background color of {@code COLOR_LABEL} to allow the user
         * to see the color they chose visually.
         * */
        private static void colorSliderAttribute() {
            Color sliderColor = getSliderColor();
            PANEL.setBackground(sliderColor);
            COLOR_TEXT.setText(colorText(sliderColor));
        }
        public static int getSettings() {
            return SETTINGS;
        }

        /** @return a color that is constructed via the values of the sliders.
         * */
        public static Color getSliderColor() {
            return new Color(RED_SLIDER.getValue(), GREEN_SLIDER.getValue(), BLUE_SLIDER.getValue());
        }

        /** A function that changes what coordinates are displayed on {@code LOC_LABEL}.
         * @param x the horizontal location.
         * @param y the vertical location.
         * */
        public static void changeLocLabelText(int x, int y) {
            LOC_LABEL.setText("Mouse Location: (x:%d, y:%d)".formatted(x, y));
        }

        /** Mutator for the 3 color sliders by getting the rgb values of the input.
         * @param color the color that the slider is being set to.
         * */
        public static void setColorSlider(Color color) {
            RED_SLIDER.setValue(color.getRed());
            GREEN_SLIDER.setValue(color.getGreen());
            BLUE_SLIDER.setValue(color.getBlue());
        }

        /** @return stroke slider's value.
         * */
        public static int getStrokeSize() {
            return STROKE_SLIDER.getValue();
        }
    }

    /** Class defining the canvas portion of the main panel.
     * */
    static class Canvas extends JPanel implements MouseInputListener {
        public static final byte SET = 6;
        public static final byte WRITE = 5;
        public static final byte MIX = 4;
        public static final byte COPY_COLOR = 3;
        public static final byte FILL = 2;
        public static final byte ERASE = 1;
        public static final byte DRAW = 0;
        private static final Color BG_COLOR = new Color(0xEEEEEE);
        private final BufferedImage image;
        private boolean hasDrawings = false;

        /** Constructor detailing values and data for the user to draw on.
         * Variables defined includes {@code image} which helps save whatever the player may draw on.
         * */
        public Canvas() {
            super();
            setLayout(null);
            setPreferredSize(new Dimension(1720, 1030));
            addMouseListener(this);
            addMouseMotionListener(this);

            this.image = new BufferedImage(1720, 1030, BufferedImage.TYPE_3BYTE_BGR);
            Graphics g = image.getGraphics();
            g.setColor(BG_COLOR);
            g.fillRect(0, 0, 1720, 1030);
        }

        /** A method that utilizes the {@code getSettings()} which determines what to do in the situation.
         * A value of {@code DRAW} draws an oval in the mouse's location, a value of {@code ERASE} erases a square at the mouse's location,
         * a value of {@code MIX} paints the entire canvas a certain color, and a value of {@code COPY_COLOR} sets the color sliders to the
         * color of the pixel the mouse is on. If the setting is not {@code COPY_COLOR}, the program will interpret
         * any calls to this method as the same thing as the user drawing on the canvas.
         * */
        private void paint(MouseEvent e) {
            Color color = OptionPanel.getSliderColor();
            int x = e.getX();
            int y = e.getY();
            OptionPanel.changeLocLabelText(x, y);
            if (OptionPanel.getSettings() == COPY_COLOR) {
                Color pxColor = getPixel(x, y);
                OptionPanel.setColorSlider(pxColor);
            } else {
                int size = OptionPanel.getStrokeSize();
                x -= size / 2;
                y -= size / 2;
                switch (OptionPanel.getSettings()) {
                    case ERASE -> erase(x, y, size);
                    case DRAW -> draw(x, y, size, color);
                    case FILL -> setBackground(color);
                    case MIX -> mix(x, y, size);
                    case WRITE -> writeText(x, y, size, color);
                    case SET -> obtainColorSet(x, y, size);
                }
            }
        }

        private void obtainColorSet(int x, int y, int size) {
            int xLimit = Math.min(x + size, getWidth());
            int yLimit = Math.min(y + size, getHeight());
            int xMin = Math.max(x - size, 0);
            int yMin = Math.max(y - size, 0);
            HashSet<Color> colors = new HashSet<>();
            for (int i = xMin; i < xLimit; i++) {
                for (int j = yMin; j < yLimit; j++) {
                    int rgb = this.image.getRGB(i, j);
                    colors.add(new Color(rgb));
                }
            }
            ArrayList<Color> list = new ArrayList<>(colors);
            list.sort(Paint::compareColor);
            StringBuilder hexList = getStringBuilder(list);
            try {
                int ans = JOptionPane.showConfirmDialog(null, "The colors on this canvas are: \n" + hexList + "\ncopy colors?",
                        "color list", JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION) {
                    String str = hexList.toString().replaceAll("[,\\n] ", "\t").toUpperCase();
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(str), null);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "The selected segment has has way too many colors!");
            }
        }

        private static StringBuilder getStringBuilder(ArrayList<Color> list) {
            StringBuilder hexList = new StringBuilder();
            int count = 0;
            for (Color color : list) {
                StringBuilder format = new StringBuilder("%x".formatted(color.getRGB() - 0xFF000000));
                while (format.length() < 6) {
                    format.insert(0, "0");
                }
                hexList.append(format);
                if (count < list.size() - 1) {
                    hexList.append(count % 12 == 11 ? "\n" : ", ");
                }
                count++;
            }
            return hexList;
        }

        /** Helper method that allows the user to draw on the canvas.
         * @param x horizontal starting location defined relative to the leftmost side that the shape begins at.
         * @param y vertical starting location defined relative to the topmost side that the shape begins at.
         * @param size the shape's width and height.
         * @param color the color of the filled shape.
         * */
        private void draw(int x, int y, int size, Color color) {
            Graphics g = getGraphics();
            g.setColor(color);
            g.fillOval(x, y, size, size);

            Graphics image = this.image.getGraphics();
            image.setColor(color);
            image.fillOval(x, y, size, size);

            this.hasDrawings = true;
        }

        /** Helper method that removes any color changes in the graphics.
         * @param x the horizontal starting location, defined relative to the leftmost side.
         * @param y the vertical starting location, defined relative to the topmost side.
         * @param size the width and height of the area being erased.
         * */
        private void erase(int x, int y, int size) {
            Graphics g = getGraphics();
            g.clearRect(x, y, size, size);

            Graphics image = this.image.getGraphics();
            image.setColor(BG_COLOR);
            image.fillRect(x, y, size, size);
            this.hasDrawings = true;
        }

        /** Takes the average of all the colors within the vicinity of size using a for loop to add all the colors up
         * before dividing them by the number of pixels, and paints over the area with the average of all the colors.
         * @param x the x location on the panel.
         * @param y the y location on the panel.
         * @param size the area being painted over and taken the color of.
         * */
        private void mix(int x, int y, int size) {
            int xLimit = Math.min(x + size, getWidth());
            int yLimit = Math.min(y + size, getHeight());
            int divider = 0;
            int red = 0;
            int green = 0;
            int blue = 0;
            for (int i = x; i < xLimit; i += 5) {
                for (int j = y; j < yLimit; j += 5) {
                    Color color = getPixel(i, j);
                    red += color.getRed();
                    green += color.getGreen();
                    blue += color.getBlue();
                    divider++;
                }
            }
            if (divider > 0) {
                red /= divider;
                green /= divider;
                blue /= divider;
                Color color = new Color(red, green, blue);
                draw(x, y, size, color);
            }
            this.hasDrawings = true;
        }

        /** First asks the user what they'd like to write on the canvas then taking it to write the message on both the canvas and buffered image.
         * @param x horizontal location where the text would begin, defined relative to the leftmost side.
         * @param y vertical location where the text would begin, defined relative to the uppermost side.
         * @param size font size of the user's input text.
         * @param color the color of the user's input text.
         * */
        private void writeText(int x, int y, int size, Color color) {
            String text = JOptionPane.showInputDialog(null, "What would you like to write on the canvas?", "Text writer");
            if (text != null) {
                Graphics g = getGraphics();
                g.setColor(color);
                g.setFont(new Font(Font.SERIF, Font.PLAIN, size * 4));
                g.drawString(text, x, y);

                Graphics image = this.image.getGraphics();
                image.setColor(color);
                image.setFont(new Font(Font.SERIF, Font.PLAIN, size * 4));
                image.drawString(text, x, y);
            }
            this.hasDrawings = true;
        }

        /** Gets the pixel of the image stored within.
         * @param x the horizontal location in the image defined relative to the leftmost side.
         * @param y the vertical location in the image defined relative to the topmost side.
         * @return the color value of the pixel at the specified location.
         * */
        public Color getPixel(int x, int y) {
            return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? new Color(this.image.getRGB(x, y)) : BG_COLOR;
        }

        /** Saves image using a buffer that captures the image on the canvas.
         * It should allow the user to make a png or jpg after being saved.
         * A file chooser is first made that allows the user to pick the directory they'd like to save the file in,
         * which is then followed by asking the user for the name and writes the file if the name is applicable.
         * */
        public void saveImage() {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter(".png and .jpg files", "png", "jpg"));
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    if (!(chooser.getSelectedFile().exists() && JOptionPane.showConfirmDialog(null,
                            "File already exists. Overwrite?", "Overwrite!", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)) {
                        ImageIO.write(image, "png", chooser.getSelectedFile());
                    }
                }
            } catch (IOException ignored) {
                JOptionPane.showMessageDialog(null, "Unable to save image", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }

        /** Loads an image via a file chooser that saves the image to a buffer which is then
         * painted over the canvas via graphics.
         * */
        public void loadFile() {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter(".png and .jpg files", "png", "jpg"));
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    BufferedImage image = ImageIO.read(chooser.getSelectedFile());
                    getGraphics().drawImage(image, 0, 0, null);
                    this.image.getGraphics().drawImage(image, 0, 0, null);
                }
            } catch (IOException ignored) {
                JOptionPane.showMessageDialog(null, "Unable to open image", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }

        /** Erases the entire canvas including the fill and all actions the users have done on the canvas.
         * It does NOT reset {@code OptionPanel}.
         * */
        public void reset() {
            getGraphics().clearRect(0, 0, getWidth(), getHeight());
            Graphics g = this.image.getGraphics();
            g.clearRect(0, 0, getWidth(), getHeight());
            g.setColor(BG_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        /** Checks whether the panel has drawings on it.
         * */
        public boolean hasDrawings() {
            return hasDrawings;
        }

        /** Applies the {@code setBackground()} method from the {@link JComponent} class to the buffer image
         * stored in the code.
         * */
        @Override
        public void setBackground(Color bg) {
            if (this.image != null) {
                Graphics g = image.getGraphics();
                g.setColor(bg);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.setBackground(bg);
        }

        /** Paints over the image using the buffered image to save the user's drawings as to save the image when
         * minimized or the panel's location on screen is changed.
         * */
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawImage(this.image, 0, 0, null);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            paint(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseDragged(MouseEvent e) {
            paint(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            OptionPanel.changeLocLabelText(e.getX(), e.getY());
        }
    }
}