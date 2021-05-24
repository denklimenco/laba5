package com.company;
import java.awt.geom.Rectangle2D;

// Этот класс является подклассом FractalGenerator. Он используется для вычисления фрактала Мандельброта.
public class Mandelbrot extends FractalGenerator {
    // Константа для количества максимальных итераций.
    public static final int MAX_ITERATIONS = 2000;

    /**
     * Этот метод позволяет генератору фракталов указать, какая часть комплексной плоскости наиболее интересна для фрактала.
     * Ему передается объект прямоугольника, и метод изменяет поля прямоугольника,
     * чтобы показать правильный начальный диапазон для фрактала.
     * Эта реализация устанавливает начальный диапазон в (-2 - 1,5i) - (1 + 1,5i) или x = -2, y = -1,5, width = height = 3.
     */
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }

    /**
     * Этот метод реализует итерационную функцию для фрактала Мандельброта.
     * Он принимает два двойных значения для действительной и мнимой частей комплексной плоскости
     * и возвращает количество итераций для соответствующей координаты.
     */
    public int numIterations(double x, double y) {
        int iteration = 0;
        double zreal = 0;
        double zimaginary = 0;
        while (iteration < MAX_ITERATIONS && zreal * zreal + zimaginary * zimaginary < 4) {
            double zrealUpdated = zreal * zreal - zimaginary * zimaginary + x;
            double zimaginaryUpdated = 2 * zreal * zimaginary + y;
            zreal = zrealUpdated;
            zimaginary = zimaginaryUpdated;
            iteration += 1;
        }
        // Если алгоритм дошел до значения MAX_ITERATIONS нужно вернуть -1, чтобы показать, что точка не выходит за границы.
        if (iteration == MAX_ITERATIONS) return -1;
        return iteration;
    }
}
