import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
//import javax.swing.plaf.metal.MetalButtonUI;

public class Main {

    public static class MinesweeperField {

        final String bomb_symbol = "*";
        final int player_size = 1;
        String[][] field;
        String[][] available_field;
        int row_size, col_size, bomb_count;
        private int player_row, player_col;
        private final ArrayList<String> checked_points = new ArrayList<>();

        public MinesweeperField(int row_size, int col_size) {
            this.row_size = row_size;
            this.col_size = col_size;
            this.field = new String[row_size][col_size];
            this.available_field = new String[row_size][col_size];
            this.bomb_count = 0;
            for (String[] strings : field) {
                Arrays.fill(strings, "0");
            }
            for (String[] strings : available_field) {
                Arrays.fill(strings, "x");
            }
        }

        public void add_player(int player_row, int player_col) {
            this.player_row = player_row;
            this.player_col = player_col;
            field[player_row][player_col] = "Я";
        }

        public void random_bombs(@NotNull String property, int value) {
            Random r_x = new Random();
            Random r_y = new Random();
            int bombs = 0;
            int required_bomb_count;
            if (property.equals("проценты")) {
                required_bomb_count = row_size * col_size * value / 100;
            } else if (property.equals("количество")) {
                required_bomb_count = value;
            } else {
                required_bomb_count = row_size * col_size * 15 / 100;
            }
            while (bombs < required_bomb_count) {
                int row = Math.round(r_x.nextFloat() * (row_size - 1));
                int col = Math.round(r_y.nextFloat() * (col_size - 1));
                if (add_bomb(row, col)) {
                    bombs += 1;
                }
            }
        }

        public void random_bombs() {
            Random r_x = new Random();
            Random r_y = new Random();
            int bombs = 0;
            int required_bomb_count = row_size * col_size * 15 / 100;
            while (bombs < required_bomb_count) {
                int row = Math.round(r_x.nextFloat() * (row_size - 1));
                int col = Math.round(r_y.nextFloat() * (col_size - 1));
                if (add_bomb(row, col)) {
                    bombs += 1;
                }
            }
        }

        private static boolean isValidIndex(String[][] arr, int row, int col) {
            return (row >= 0) && (row < arr.length) && (col >= 0) && (col < arr[row].length);
        }

        public boolean add_bomb(int row, int col) {
            boolean near_player = false;
            for (int i = -player_size; i <= player_size; i++) {
                for (int j = -player_size; j <= player_size; j++) {
                    if ((row == player_row + i) && (col == player_col + j)) {
                        near_player = true;
                        break;
                    }
                }
            }
            if (near_player) {
                System.out.println("near player " + row + " " + col);
                return false;
            }
            if ((row >= 0 && row < field.length) && (col >= 0 && row < field[row].length)) {
                if (!field[row][col].equals(bomb_symbol)) {
                    field[row][col] = bomb_symbol;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if (isValidIndex(field, row + i, col + j)) {
                                if (!field[row + i][col + j].equals(bomb_symbol)) {
                                    int cell = Integer.parseInt(field[row + i][col + j]);
                                    field[row + i][col + j] = String.valueOf(cell + 1);
                                }
                            }
                        }
                    }
                    System.out.println("added " + row + " " + col);
                    this.bomb_count += 1;
                    return true;
                } else {
                    System.out.println("skipped " + row + " " + col);
                    return false;
                }
            } else {
                System.out.println("skipped " + row + " " + col);
                return false;
            }
        }

        public void open_void_on_field(int row, int col) {
            if (field[row][col].equals("0") || field[row][col].equals("Я")) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (isValidIndex(available_field, row + i, col + j)) {
                            String next_point = (row + i) + " " + (col + j);
                            if (!checked_points.contains(next_point)) {
                                available_field[row + i][col + j] = field[row + i][col + j];
                                checked_points.add(next_point);
                                System.out.println(checked_points);
                                open_void_on_field(row + i, col + j);
                            }
                        }
                    }
                }
            }
        }

        public void show_full_field(String[][] field) {
            System.out.println();
            System.out.println("Поле игры \"Сапёр\" размером " + this.row_size + "x" + this.col_size);
            System.out.println("Количество бомб: " + this.bomb_count);
            System.out.println("Плотность бомб: " + (100 * ((float) this.bomb_count / (this.row_size * this.col_size))) + " %");
            for (int i = 0; i < field[0].length; i++)
                System.out.print("==");
            System.out.println();
            for (String[] strings : field) {
                for (String cell : strings) {
                    if (cell.equals("0")) {
                        System.out.print("_ ");
                    } else {
                        System.out.print(cell + " ");
                    }
                }
                System.out.println();
            }
            for (int i = 0; i < field[0].length; i++)
                System.out.print("==");
            System.out.println();
        }

        public void show_full_field(String[][] field, boolean show_zero) {
            System.out.println();
            System.out.println("Поле игры \"Сапёр\" размером " + this.row_size + "x" + this.col_size);
            System.out.println("Количество бомб: " + this.bomb_count);
            System.out.println("Плотность бомб: " + (100 * ((float) this.bomb_count / (this.row_size * this.col_size))) + " %");
            for (int i = 0; i < field[0].length; i++)
                System.out.print("==");
            System.out.println();
            for (String[] strings : field) {
                for (String cell : strings) {
                    if (show_zero) {
                        System.out.print(cell + " ");
                    } else {
                        if (cell.equals("0")) {
                            System.out.print("_ ");
                        } else {
                            System.out.print(cell + " ");
                        }
                    }
                }
                System.out.println();
            }
            for (int i = 0; i < field[0].length; i++)
                System.out.print("==");
            System.out.println();
        }
    }

    public static class MinesweeperWindow {

        private static MinesweeperField minesField;

        private static JFrame frame;

        private static JPanel panel_field;

        private static JPanel global_panel;

        private static JPanel start_panel;

        private static JLabel text_info;

        private static JPanel size_panel;

        private static JPanel error_panel;

        private static JPanel panel_alignment;

        private static JTextField set_rows_size;

        private static JTextField set_cols_size;

        private static final String flag_symbol = "¶";

        private static boolean game_has_started = false;

        private static final Map<String, Color> numbers_color = new HashMap<>();

        private static int time_from_start = -1;

        private static boolean pause_or_final = false;

        private static boolean opened_size_menu = false;

        private static boolean pause = false;

        private static int row_size = 8;
        private static int col_size = 8;

        private static Thread timer = new Thread(new Runnable() {
            public void run() {
                while (true) { //бесконечно крутим
                    if (!pause_or_final) {
                        try {
                            time_from_start += 1;
                            int seconds = time_from_start % 60;
                            int minutes = (time_from_start % 3600) / 60;
                            int hours = time_from_start / 3600;
                            text_info.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                            Thread.sleep(1000); // 4 секунды в милисекундах
                        } catch (InterruptedException e) {
                            break;
                        }
                    } else {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        static class ThisMouseListener extends JFrame implements MouseListener {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof ThisButton button) {
                    String[] components = button.getName().split(" ");
                    if (components.length == 3 && components[0].equals("button")) {
                        int row = Integer.parseInt(components[1]);
                        int col = Integer.parseInt(components[2]);
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            System.out.println("Left button in [" + row + ", " + col + "] pressed");
                            if (!game_has_started) {
                                time_from_start = -1;
                                pause_or_final = false;
                                try {
                                    timer.start();
                                } catch (IllegalThreadStateException ignored) {
                                }
                                game_has_started = true;
                                minesField = new MinesweeperField(row_size, col_size);
                                minesField.add_player(row, col);
                                minesField.random_bombs();
                                minesField.open_void_on_field(row, col);
                                minesField.show_full_field(minesField.field);
                                minesField.show_full_field(minesField.available_field);
                                update_field();
                            } else {
                                if (button.isUsable()) {
                                    if (!button.getText().equals(flag_symbol)) {
                                        if (minesField.field[row][col].equals("*")) {
                                            game_over(button, "click");
                                        } else if (minesField.field[row][col].equals("0")) {
                                            minesField.open_void_on_field(row, col);
                                            update_field();
                                        } else {
                                            minesField.available_field[row][col] = minesField.field[row][col];
                                            update_field();
                                        }
                                    }
                                } else {
                                    if ("12345678".contains(button.getText())) {
                                        int bomb_near = Integer.parseInt(button.getText());
                                        int bomb_count = 0;
                                        for (int i = -1; i <= 1; i++) {
                                            for (int j = -1; j <= 1; j++) {
                                                if (MinesweeperField.isValidIndex(minesField.available_field, row + i, col + j)) {
                                                    if (minesField.available_field[row + i][col + j].contains(flag_symbol)) {
                                                        bomb_count += 1;
                                                    }
                                                }
                                            }
                                        }
                                        if (bomb_count == bomb_near) {
                                            for (int i = -1; i <= 1; i++) {
                                                for (int j = -1; j <= 1; j++) {
                                                    if (MinesweeperField.isValidIndex(minesField.available_field, row + i, col + j)) {
                                                        if (minesField.field[row + i][col + j].equals("*") && minesField.available_field[row + i][col + j].equals(flag_symbol)) {

                                                        } else if (minesField.field[row + i][col + j].equals("*")) {
                                                            game_over(button, "near");
                                                            break;
                                                        } else if (minesField.field[row + i][col + j].equals("0")) {
                                                            minesField.open_void_on_field(row + i, col + j);
                                                            update_field();
                                                        } else {
                                                            minesField.available_field[row + i][col + j] = minesField.field[row + i][col + j];
                                                            update_field();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                check_win();
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3 && button.isUsable()) {
                            System.out.println("Right button in [" + row + ", " + col + "] pressed");
                            if (game_has_started) {
                                String cell = minesField.available_field[row][col];
                                if (cell.equals("x") || cell.equals(flag_symbol)) {
                                    if (button.getText().equals(flag_symbol)) {
                                        button.setText(" ");
                                        button.setForeground(Color.BLACK);
                                        minesField.available_field[row][col] = "x";
                                    } else {
                                        button.setText(flag_symbol);
                                        button.setForeground(numbers_color.get("flag"));
                                        minesField.available_field[row][col] = flag_symbol;
                                    }
                                    button.setFont(new Font("TimesRoman", Font.BOLD, 25));
                                }
                            }
                            update_field();
                        }
                    } else if (components[0].equals("Новая")) {
                        new_game();
                    } else if (components[0].equals("В")) { // меню
                        global_panel.setVisible(false);
                        start_panel.setVisible(!global_panel.isVisible());
                        size_panel.setVisible(false);
                        opened_size_menu = false;
                    } else if (components[0].equals("Пауза") || components[0].equals("Возобновить")) {
                        if (pause) {
                            button.setText("Пауза");
                            pause = false;
                            pause_or_final = false;
                            update_field();
                        } else {
                            if (!pause_or_final){
                                button.setText("Возобновить");
                                pause = true;
                                pause_or_final = true;
                                hide_field();
                            }
                        }
                    } else if (components[0].equals("edit")) {
                        if (!opened_size_menu) {
                            size_panel.setVisible(true);
                            opened_size_menu = true;
                        } else {
                            size_panel.setVisible(false);
                            opened_size_menu = false;
                        }
                    } else if (components[0].equals("play")) {
                        error_panel.setVisible(false);
                        System.out.println(set_rows_size.getText() + " " + set_cols_size.getText());
                        try {
                            row_size = Integer.parseInt(set_rows_size.getText());
                            col_size = Integer.parseInt(set_cols_size.getText());

                            if (row_size >= 11 || col_size >= 11 || row_size <= 3 || col_size <= 3) {
                                throw new NumberFormatException();
                            }

                            global_panel.setVisible(true);
                            start_panel.setVisible(!global_panel.isVisible());
                            try {
                                global_panel.remove(3);

                            } catch (ArrayIndexOutOfBoundsException ignored) {
                            }
                            create_field(global_panel);
                            new_game();
                        } catch (NumberFormatException exception) {
                            error_panel.setVisible(true);
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        }

        static class ThisButton extends JButton {
            private boolean usable = true;

            public ThisButton(String name) {
                super(name);
            }

            public boolean isUsable() {
                return usable;
            }

            public void setUsable(boolean usable) {
                this.usable = usable;
            }
        }

        MinesweeperWindow() {
            frame = new JFrame("MineSweeper");
            frame.setMinimumSize(new Dimension(800, 800));
//            frame.setMaximumSize(new Dimension(1000, 1000));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // START_PANEL

            start_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            start_panel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            frame.add(start_panel);

            JPanel logo_panel = new JPanel(new GridLayout());
            logo_panel.setPreferredSize(new Dimension(frame.getWidth() - 100, frame.getHeight() / 2));
            logo_panel.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(Color.black),
                    new EmptyBorder(5, 0, 0, 0)
            ));
            logo_panel.setAlignmentX(200);
            logo_panel.setBackground(Color.decode("#43A047"));
            start_panel.add(logo_panel);

            JLabel text_start = new JLabel(new ImageIcon("icon.jpg"));
            text_start.setPreferredSize(new Dimension(50, 50));
            text_start.setVerticalAlignment(SwingConstants.BOTTOM);
            text_start.setHorizontalAlignment(SwingConstants.CENTER);
            logo_panel.add(text_start);

            JPanel start_buttons_panel = new JPanel();
            start_buttons_panel.setPreferredSize(new Dimension(frame.getWidth() - 100, 100));
            start_buttons_panel.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(Color.black),
                    new EmptyBorder(5, 0, 0, 0)
            ));
            start_buttons_panel.setBackground(Color.decode("#212121"));
            start_panel.add(start_buttons_panel);

            JButton go = new_button(0, 0, 55);
            go.setText("Играть");
            go.setName("play");
            go.setPreferredSize(new Dimension(200, 70));
            go.setFont(new Font("TimesRoman", Font.BOLD, 20));
            start_buttons_panel.add(go);

            JLabel go_edit_align = new JLabel();
            go_edit_align.setPreferredSize(new Dimension(20, 50));
            start_buttons_panel.add(go_edit_align);

            JButton edit_size = new_button(0, 0, 55);
            edit_size.setText("Свой размер");
            edit_size.setName("edit");
            edit_size.setPreferredSize(new Dimension(200, 70));
            edit_size.setFont(new Font("TimesRoman", Font.BOLD, 20));
            start_buttons_panel.add(edit_size);

            size_panel = new JPanel();
            size_panel.setPreferredSize(new Dimension(frame.getWidth() - 100, 75));
            size_panel.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(Color.black),
                    new EmptyBorder(5, 0, 0, 0)
            ));
            start_panel.add(size_panel);

            set_rows_size = new JTextField();
            set_rows_size.setPreferredSize(new Dimension(100, 50));
            set_rows_size.setText("8");
            set_rows_size.setHorizontalAlignment(SwingConstants.CENTER);
            size_panel.add(set_rows_size);

            set_cols_size = new JTextField();
            set_cols_size.setPreferredSize(new Dimension(100, 50));
            set_cols_size.setText("8");
            set_cols_size.setHorizontalAlignment(SwingConstants.CENTER);
            size_panel.add(set_cols_size);

            error_panel = new JPanel();
            error_panel.setPreferredSize(new Dimension(frame.getWidth() - 100, 75));
            error_panel.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(Color.black),
                    new EmptyBorder(17, 0, 0, 0)
            ));
            error_panel.setBackground(Color.decode("#BF360C"));
            start_panel.add(error_panel);

            JLabel error_text = new JLabel();
            error_text.setText("Извините, но данный размер не поддерживается :с");
            error_text.setFont(new Font("TimesRoman", Font.BOLD, 20));
            error_panel.add(error_text);

            error_panel.setVisible(false);

            size_panel.setVisible(false);

            // GLOBAL_PANEL

            global_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            global_panel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            frame.add(global_panel);

            JPanel panel_service = new JPanel();
            panel_service.setPreferredSize(new Dimension(frame.getWidth() - 100, 50));
            panel_service.setBorder(BorderFactory.createLineBorder(Color.black));
            panel_service.setBackground(Color.decode("#212121"));
            global_panel.add(panel_service, BorderLayout.NORTH);

            for (int i = 0; i < 4; i++) {
                String name = "";
                switch (i) {
                    case 0 -> name = "Новая игра";
                    case 1 -> name = "Пауза";
                    case 2 -> name = "В меню";
                    case 3 -> name = "Об игре";
                }
                ThisButton button = new_button(i, i, 55);
                button.setText(name);
                button.setName(name);
                button.setPreferredSize(new Dimension(150, 40));
                panel_service.add(button, BorderLayout.CENTER);
            }

            JPanel panel_info = new JPanel();
            panel_info.setPreferredSize(new Dimension(frame.getWidth() - 100, 50));
            panel_info.setBackground(Color.decode("#212121"));
            panel_info.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(Color.black),
                    new EmptyBorder(5, 0, 0, 0)
            ));
            global_panel.add(panel_info, BorderLayout.NORTH);

            text_info = new JLabel();
            text_info.setText("Сапёр");
            text_info.setFont(new Font("TimesRoman", Font.BOLD, 25));
            text_info.setBackground(Color.decode("#212121"));
            text_info.setForeground(Color.WHITE);
            panel_info.add(text_info, BorderLayout.CENTER);

            panel_alignment = new JPanel();
            panel_alignment.setPreferredSize(new Dimension(frame.getWidth() - 100, (frame.getHeight() - 10) / 2 - 345));
            global_panel.add(panel_alignment, BorderLayout.NORTH);

            frame.addComponentListener(new ComponentListener() {
                public void componentResized(ComponentEvent e) {
                    Rectangle f_size = e.getComponent().getBounds();
                    System.out.println("Resized! - " + f_size.width + "x" + f_size.height);
                    try {
                        global_panel.setBounds(0, 0, f_size.width, f_size.height);
                        panel_service.setPreferredSize(new Dimension(f_size.width - 100, 50));
                        panel_info.setPreferredSize(new Dimension(f_size.width - 100, 50));
                        panel_alignment.setPreferredSize(new Dimension(f_size.width - 100, (f_size.height - 10) / 2 - panel_field.getHeight() / 2 - 100));

                    } catch (NullPointerException ignored) {

                    } finally {
                        start_panel.setBounds(0, 0, f_size.width, f_size.height);
                        logo_panel.setPreferredSize(new Dimension(f_size.width - 100, f_size.height / 2));
                        start_buttons_panel.setPreferredSize(new Dimension(f_size.width - 100, 100));
                        size_panel.setPreferredSize(new Dimension(f_size.width - 100, 75));
                        error_panel.setPreferredSize(new Dimension(f_size.width - 100, 75));
                    }
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                }

                @Override
                public void componentShown(ComponentEvent e) {
                }

                @Override
                public void componentHidden(ComponentEvent e) {
                }
            });

            global_panel.setVisible(false);
            start_panel.setVisible(!global_panel.isVisible());
        }

        private static ThisButton new_button(int row, int col, int size) {
            ThisButton new_button = new ThisButton(row + "" + col);

            new_button.setUsable(true);
            new_button.setName("button " + row + " " + col);
            new_button.setPreferredSize(new Dimension(size, size));

            if (new_button.getText().length() == 2) {
                new_button.setFont(new Font("TimesRoman", Font.BOLD, 15));
            } else {
                new_button.setFont(new Font("TimesRoman", Font.BOLD, 30));
            }

            if (!UIManager.getLookAndFeel().getClass().getName().equals(UIManager.getSystemLookAndFeelClassName())) {
                new_button.setContentAreaFilled(false);
            }

            new_button.setFocusPainted(false);
            new_button.setHorizontalTextPosition(SwingConstants.CENTER);
            new_button.addMouseListener(new ThisMouseListener());

            return new_button;
        }

        public static void create_field(JPanel global_panel) {
            int button_size = 55;

            int panel_width = col_size * (button_size + 5) + 10;
            int panel_height = row_size * (button_size + 5) + 10;

            panel_field = new JPanel();
            panel_field.setPreferredSize(new Dimension(panel_width, panel_height));
            panel_field.setBorder(BorderFactory.createLineBorder(Color.black));
            global_panel.add(panel_field, BorderLayout.CENTER);

            for (int i = 0; i < row_size; i++) {
                for (int j = 0; j < col_size; j++) {
                    panel_field.add(new_button(i, j, button_size), BorderLayout.CENTER);
                }
            }
        }

        public static void new_game() {

            game_has_started = false;

            pause_or_final = true;

            text_info.setText("Сапёр");

            for (int i = 0; i < panel_field.getComponentCount(); i++) {
                ThisButton button = (ThisButton) panel_field.getComponent(i);
                button.setText(" ");
                button.setUsable(true);
                button.setEnabled(true);
                button.setBackground(Color.GRAY);
                button.setBorderPainted(true);
                if (button.getText().length() == 2) {
                    button.setFont(new Font("TimesRoman", Font.BOLD, 15));
                } else {
                    button.setFont(new Font("TimesRoman", Font.BOLD, 30));
                }
            }

            numbers_color.put("flag", Color.RED);
            numbers_color.put("1", Color.decode("#76FF03"));
            numbers_color.put("2", Color.decode("#C6FF00"));
            numbers_color.put("3", Color.decode("#FFEA00"));
            numbers_color.put("4", Color.decode("#FFC400"));
            numbers_color.put("5", Color.decode("#FF9100"));
            numbers_color.put("6", Color.decode("#FF3D00"));
            numbers_color.put("7", Color.decode("#FF3D00"));
            numbers_color.put("8", Color.decode("#FF3D00"));
        }

        public static void update_field() {

            String[][] field = minesField.available_field;

            int count = -1;

            for (int i = 0; i < row_size; i++) {
                for (int j = 0; j < col_size; j++) {
                    count += 1;
                    ThisButton button = (ThisButton) panel_field.getComponent(count);
                    if (field[i][j].equals("0") || field[i][j].equals("Я")) {
                        button.setUsable(false);
                        button.setEnabled(false);
                        button.setBackground(Color.LIGHT_GRAY);
                        button.setBorderPainted(false);
                    } else if (field[i][j].equals(flag_symbol)) {
                        button.setUsable(true);
                        button.setEnabled(true);
                        button.setText(field[i][j]);
                        button.setBackground(Color.PINK);
                    } else if (!field[i][j].equals("x")) {
                        button.setText(field[i][j]);
                        button.setForeground(numbers_color.get(field[i][j]));
                        button.setFont(new Font("TimesRoman", Font.BOLD, 25));
                        button.setUsable(false);
                        button.setEnabled(true);
                        button.setBackground(Color.BLACK);
                        button.setBorderPainted(false);
                    } else {
                        button.setUsable(true);
                        button.setEnabled(true);
                        button.setBackground(Color.WHITE);
                    }
                }
            }
        }

        public static void hide_field() {

            for (int i = 0; i < panel_field.getComponentCount(); i++) {
                ThisButton button = (ThisButton) panel_field.getComponent(i);
                button.setUsable(false);
                button.setEnabled(false);
                button.setText("");
            }
        }

        public static void check_win() {
            Map<String, Integer> counter = new HashMap<>();
            for (String[] x_row : minesField.available_field) {
                for (String x : x_row) {
                    int newValue = counter.getOrDefault(x, 0) + 1;
                    counter.put(x, newValue);
                }
            }
            try {
                if (!counter.containsKey(flag_symbol) && counter.get("x") == minesField.bomb_count ||
                        !counter.containsKey("x") && counter.get(flag_symbol) == minesField.bomb_count ||
                        counter.get("x") + counter.get(flag_symbol) == minesField.bomb_count) {
                    pause_or_final = true;
                    int count = -1;
                    for (int i = 0; i < minesField.field.length; i++) {
                        for (int j = 0; j < minesField.field[i].length; j++) {
                            count += 1;
                            ThisButton bomb_button = (ThisButton) panel_field.getComponent(count);
                            bomb_button.setUsable(false);
                            if (minesField.field[i][j].equals("*")) {
                                bomb_button.setText("†");
                                bomb_button.setFont(new Font("TimesRoman", Font.BOLD, 35));
                                bomb_button.setForeground(Color.RED);
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Поздравляем! Вы выиграли! Время: " + time_from_start + " с.");
                    System.out.println(time_from_start);
                }
            } catch (NullPointerException ignored) {
            }
        }

        public static void game_over(ThisButton button, String type) {
            pause_or_final = true;
            int count = -1;
            for (int i = 0; i < minesField.field.length; i++) {
                for (int j = 0; j < minesField.field[i].length; j++) {
                    count += 1;
                    ThisButton bomb_button = (ThisButton) panel_field.getComponent(count);
                    bomb_button.setUsable(false);
                    if (minesField.field[i][j].equals("*")) {
                        bomb_button.setText("†");
                        bomb_button.setFont(new Font("TimesRoman", Font.BOLD, 35));
                        bomb_button.setForeground(Color.RED);
                    }
                }
            }
            if (type.equals("click")) {
                button.setForeground(Color.WHITE);
            }
            JOptionPane.showMessageDialog(frame, "Вы проиграли!");
        }

        public void show_window() {
            frame.setLayout(null);
            frame.setVisible(true);
        }
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            // GTK look and feel (Linux)
//          UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());     // The Java(tm) Look and Feel

        MinesweeperWindow x = new MinesweeperWindow();
        x.show_window();
    }
}


//
//import javax.swing.*;
//        import java.awt.*;
//        import java.util.Random;
//
//public class Main {
//    public static void main(String args[]) {
//        EventQueue.invokeLater(() -> {
//            JFrame f = new JFrame();
//            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            f.setSize(400, 400);
//            f.setLocationRelativeTo(null);
//
//            Random rand = new Random();
//
//            MyButton b = new MyButton("Change background");
//            b.addActionListener(event -> {
//                if (b.isUsable()) {
//                    f.getContentPane().setBackground(new Color(rand.nextInt(Integer.MAX_VALUE)));
//                }
//            });
//
//            JCheckBox cb = new JCheckBox("Usable");
//            cb.setOpaque(false);
//            cb.setSelected(true);
//            cb.addActionListener(event -> {
//                b.setUsable(cb.isSelected());
//            });
//
//            f.getContentPane().setLayout(new FlowLayout());
//            f.add(b);
//            f.add(cb);
//            f.setVisible(true);
//        });
//    }
//}
//
//class MyButton extends JButton {
//    private boolean usable = true;
//
//    public MyButton(String name) {
//        super(name);
//    }
//
//    public boolean isUsable() {
//        return usable;
//    }
//
//    public void setUsable(boolean usable) {
//        this.usable = usable;
//    }
