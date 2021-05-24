package com.company;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

// Этот класс позволяет отображать фракталы.
// Он является производным от javax.swing.JComponent.
class JImageDisplay extends JComponent
{
    // Экземпляр буферизованного изображения.
    // Управляет изображением, в содержимое которого мы можем писать.
    private BufferedImage displayImage;
    // Конструктор принимает целые значения ширины и высоты
    // и инициализирует свой объект класса BufferedImage
    // как новое изображение с этой шириной и высотой типа изображения TYPE_INT_RGB.
    public JImageDisplay(int width, int height)
    {
        displayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Вызов метода setPreferredSize() родительского класса с указаннной шириной и высотой.
        Dimension imageDimension = new Dimension(width, height);
        super.setPreferredSize(imageDimension);
    }
    // Реализация суперкласса paintComponent(g) вызывается для того,
    // чтобы границы и объекты отображались правильно.
    // Затем изображение отрисосывается в компонент.
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight(), null);
    }
    public void clearImage() // Устанавливает все пиксели в данных изображения в черный цвет.
    {
        int[] blankArray = new int[getWidth() * getHeight()];
        displayImage.setRGB(0, 0, getWidth(), getHeight(), blankArray, 0, 1);
    }
    // Устанавливает пиксель определенного цвета.
    public void drawPixel(int x, int y, int rgbColor) { displayImage.setRGB(x, y, rgbColor); }
    public BufferedImage getImage() {
        return displayImage;
    }
}
