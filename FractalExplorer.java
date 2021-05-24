package com.company;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.awt.image.*;

/** Этот класс позволяет исследовать различные части фрактала, создавая и показывая графический интерфейс Swing,
 * и обрабатывает события, вызванные различными взаимодействиями пользователя. */
public class FractalExplorer implements ItemListener {
    private int displaySize; // Целое число «размер экрана», которое является шириной и высотой отображения в пикселях.
    private JImageDisplay display; // Ссылка JImageDisplay, для обновления отображения в разных методах в процессе вычисления фрактала.
    private FractalGenerator fractal; // Объект FractalGenerator, использующий ссылку базового класса для отображения других типов фракталов в будущем.
    private Rectangle2D.Double range; // Объект Rectangle2D.Double, указывающий диапазона комплексной плоскости, которая выводится на экран.

    // Конструктор, который принимает размер отображения, сохраняет его и инициализирует объекты диапазона и генератора фракталов.
    public FractalExplorer(int size) {
        displaySize = size;
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);
    }

    // Этот метод инициализирует графический интерфейс Swing с помощью JFrame, содержащего объект JImageDisplay и кнопку для сброса отображения.
    public void createAndShowGUI() {
        // Настройка фрейма на использование java.awt.BorderLayout для его содержимого.
        display.setLayout(new BorderLayout());
        JFrame myframe = new JFrame("Fractal Explorer");

        JLabel header = new JLabel("Fractal:");
        // Добавление объекта отображения изображения в позицию BorderLayout.CENTER.
        myframe.add(display, BorderLayout.CENTER);
        // Создание кнопок и панели выбора.
        Choice Button1 = new Choice();
        Button1.add("Mandelbrot");
        Button1.add("Tricorn");
        Button1.add("Burning Ship");
        JPanel panel = new JPanel();
        myframe.add(panel, BorderLayout.NORTH);
        panel.add(header);
        panel.add(Button1);
        JButton resetButton = new JButton("Reset");
        ResetHandler handler = new ResetHandler();
        resetButton.addActionListener(handler);
        JButton saveImage = new JButton("Save Image");
        SaveHandler save = new SaveHandler();
        saveImage.addActionListener(save);
        JPanel down = new JPanel();
        myframe.add(down, BorderLayout.SOUTH);
        down.add(resetButton);
        down.add(saveImage);
        Button1.addItemListener(this);
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);
        // Установка операции закрытия фрейма по умолчанию на «выход».
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Размещение содержимого фрейма, чтобы оно было видимым и запрещение изменения размера окна.
        myframe.pack();
        myframe.setVisible(true);
        myframe.setResizable(false);
    }

    /**
     * Приватный вспомогательный метод для отображения фрактала. Этот метод просматривает каждый пиксель на дисплее
     * и вычисляет количество итераций для соответствующих координат в области отображения фрактала.
     * Если количество итераций равно -1, цвет пикселя становится черным.
     * В противном случае выбираем значение на основе количества итераций, обновляем дисплей с использованием
     * цвета для каждого пикселя и перерисовываем JImageDisplay, когда все пиксели были нарисованы.
     */
    private void drawFractal() {
        // Циклический просмотр каждого пикселя на дисплее
        for (int x = 0; x < displaySize; x++)
            for (int y = 0; y < displaySize; y++) {
                // Нахождение соответствующих координат xCoord и yCoord в области отображения фрактала.
                double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);
                // Вычисление количества итераций для координат в области отображения фрактала.
                int iteration = fractal.numIterations(xCoord, yCoord);
                if (iteration == -1) // Если количество итераций равно -1, устанавливаем пиксель черным.
                    display.drawPixel(x, y, 0);
                else {
                    // В противном случае выбираем значение оттенка в зависимости от количества итераций.
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    display.drawPixel(x, y, rgbColor); // Обновление дисплея для каждого пикселя.
                }
            }
        // Когда все пиксели нарисованы, перерисовываем JImageDisplay в соответствии с текущим содержимым его изображения.
        display.repaint();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == "Mandelbrot") fractal = new Mandelbrot();
        if (e.getItem() == "Tricorn") fractal = new Tricorn();
        if (e.getItem() == "Burning Ship") fractal = new BurningShip();
        drawFractal();
    }

    private class SaveHandler implements ActionListener // Класс для сохранения
    {
        public void actionPerformed(ActionEvent e) {

            JFileChooser myFileChooser = new JFileChooser();
            FileFilter extensionFilter = new FileNameExtensionFilter("PNG Images", "png");
            myFileChooser.setFileFilter(extensionFilter);
            myFileChooser.setAcceptAllFileFilterUsed(false);
            int userSelection = myFileChooser.showSaveDialog(display);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File file = myFileChooser.getSelectedFile();

                String file_name = file.toString();
                System.out.println(file_name.lastIndexOf(".png"));
                if (file_name.lastIndexOf(".png") == -1) {
                    file_name += ".png";
                    file = new java.io.File(file_name);
                }
                try {
                    BufferedImage displayImage = display.getImage();
                    javax.imageio.ImageIO.write(displayImage, "png", file);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(
                            display, exception.getMessage(),
                            "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class ResetHandler implements ActionListener // Внутренний класс для обработки событий ActionListener от кнопки сброса.
    {
        // Обработчик сбрасывает диапазон до начального диапазона, заданного генератором, а затем рисует фрактал.
        public void actionPerformed(ActionEvent e) {
            fractal.getInitialRange(range);
            drawFractal();
        }
    }

    private class MouseHandler extends MouseAdapter // Внутренний класс для обработки событий MouseListener с дисплея.
    {
        /**
         * Когда обработчик получает событие щелчка мыши, он сопоставляет пиксельные координаты щелчка
         * с областью отображаемого фрактала, а затем вызывает метод генератора correnterAndZoomRange() с координатами,
         * по которым щелкнули, и шкалой 0,5.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            // Получение координаты x области щелчка мыши.
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
            // Получение координаты y области щелчка мыши.
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);
            // Вызов метода генератора RecenterAndZoomRange () с координатами, по которым был выполнен щелчок, и масштабом 0,5.
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            drawFractal(); // Перерисовка фрактала после изменения отображаемой области.
        }
    }

    /**
     * Статический метод main() для запуска FractalExplorer.
     * Инициализирует новый экземпляр FractalExplorer с размером отображения 600,
     * вызывает createAndShowGUI() в объекте проводника,
     * а затем вызывает drawFractal() в проводнике.
     */
    public static void main(String[] args) {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}
