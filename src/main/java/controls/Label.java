package controls;

import io.github.humbleui.jwm.Event;
import io.github.humbleui.jwm.Window;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.TextLine;
import misc.CoordinateSystem2i;
import misc.Misc;
import panels.Panel;

import static app.Colors.LABEL_TEXT_COLOR;
import static app.Fonts.FONT12;

/**
 * Заголовок
 */

/**
 * Флаг, нужно ли выравнивать текст по центру по горизонтали
 */

/**
 * Флаг, нужно ли выравнивать текст по центру по вертикали
 */

public class Label extends Panel {
    /**
     * Текст заголовка
     */
    public String text;


    public Label(Window window, boolean drawBG, int backgroundColor, int padding, String text) {
        super(window, drawBG, backgroundColor, padding);
        this.text = text;
    }

    /**
     * Метод рисованияв конкретной реализации
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    @Override
    public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        /**
         * Метод рисованияв конкретной реализации
         *
         * @param canvas   область рисования
         * @param windowCS СК окна
         */
        @Override
        public void paintImpl(Canvas canvas, CoordinateSystem2i windowCS) {
            // сохраняем область рисования
            canvas.save();
            // создаём линию
            try (TextLine line = TextLine.make(text, FONT12)) {
                // получаем высоту текста
                int capHeight = (int) FONT12.getMetrics().getCapHeight();
                // если нужно центрировать по горизонтали
                if (centered)
                    canvas.translate((windowCS.getSize().x - line.getWidth()) / 2.0f, 0);
                if (vcentered)
                    canvas.translate(0, (windowCS.getSize().y - capHeight) / 2.0f);

                // рисуем текст
                try (Paint fg = new Paint().setColor(LABEL_TEXT_COLOR)) {
                    canvas.drawTextLine(line, 0, capHeight, fg);
                }
            }
            // восстанавливаем области рисования
            canvas.restore();
        }
        // создаём линию
        try (TextLine line = TextLine.make(text, FONT12)) {
            // получаем высоту текста
            int capHeight = (int) FONT12.getMetrics().getCapHeight();
            // рисуем текст
            try (Paint fg = new Paint().setColor(LABEL_TEXT_COLOR)) {
                canvas.drawTextLine(line, 0, capHeight, fg);



    public Label(Window window, boolean drawBG, int backgroundColor, int padding, String text,
                boolean centered, boolean vcentered) {
                    super(window, drawBG, backgroundColor, padding);
                    this.text = text;
                    this.centered = centered;
                    this.vcentered = vcentered;
                }

            }
        }
        // восстанавливаем области рисования
        canvas.restore();
    }

    /**
     * Обработчик событий
     *
     * @param e событие
     */
    @Override
    public void accept(Event e) {

    }

    protected boolean centered;
    protected boolean vcentered;
}