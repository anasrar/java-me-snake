/*
 * The MIT License
 *
 * Copyright 2023 Anas Rin <mynameanasrar@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.anasrin;

import java.util.Random;
import java.util.Stack;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * @author Anas Rin <mynameanasrar@gmail.com>
 */
public class DrawCanvas extends Canvas implements Runnable {

	public final int screenWidth = this.getWidth();
	public final int screenHeight = this.getHeight();
	public final int size = 8;
	public final int padding = 20;
	public final int row = (screenWidth - padding) / size;
	public final int column = (screenHeight - padding) / size;
	public final int areaWidth = row * size;
	public final int areaHeight = column * size;
	public final int offsetX = (screenWidth - areaWidth) / 2;
	public final int offsetY = (screenHeight - areaHeight) / 2;
	/**
	 * Snake body. The first index is the head. Can't get generic to work on
	 * Java 1.5
	 */
	public Stack snake = new Stack();
	public Translate snakeDirection = new Translate(1, 0);
	public Random random = new Random();
	public Translate food = new Translate(0, 0);
	public boolean isThreadRunning = true;
	public boolean isUpdate = false;

	/**
	 * constructor
	 */
	public DrawCanvas() {
		restart();
	}

	public void restart() {
		// Initial snake
		snake.removeAllElements();
		snake.addElement(new Translate(2, 0));
		snake.addElement(new Translate(1, 0));
		snake.addElement(new Translate(0, 0));
		snakeDirection = new Translate(1, 0);

		placeFood();

		isUpdate = false;
	}

	/**
	 * paint
	 *
	 * @param g
	 */
	public void paint(Graphics g) {
		// Clear color
		g.setColor(0x141517);
		g.fillRect(0, 0, screenWidth, screenHeight);

		// Border
		g.setColor(0x5c5f66);
		g.drawRect(offsetX, offsetY, areaWidth, areaHeight);

		// Food
		g.setColor(0xff6b6b);
		g.fillRect(offsetX + (food.x * size), offsetY + (food.y * size), size, size);

		// Snake part
		for (int i = 0; i < snake.size(); i++) {
			if (i == 0) {
				// Head color
				g.setColor(0x94d82d);
			} else {
				g.setColor(0x5c7cfa);
			}
			Translate part = (Translate) snake.elementAt(i);
			g.fillRect(offsetX + (part.x * size), offsetY + (part.y * size), size, size);
		}

		if (!isUpdate) {
			g.setFont(
				Font.getFont(
					Font.FACE_MONOSPACE,
					Font.STYLE_BOLD,
					Font.SIZE_SMALL
				)
			);
			g.setColor(0x5c5f66);
			g.drawString(
				"PRESS ANY BUTTON TO START",
				offsetX + (areaWidth / 2),
				offsetY + (areaHeight / 2),
				Graphics.BOTTOM | Graphics.HCENTER
			);
		};
	}

	protected void keyPressed(int key) {
		if (!isUpdate) {
			isUpdate = true;
		} else {
			if (key > -5 && key < 58) {
				switch (key) {
					case -1:
					case 50:
						if (snakeDirection.y != -1 && snakeDirection.y != 1) {
							snakeDirection.set(0, -1);
						}
						break;
					case -2:
					case 56:
						if (snakeDirection.y != -1 && snakeDirection.y != 1) {
							snakeDirection.set(0, 1);
						}
						break;
					case -3:
					case 52:
						if (snakeDirection.x != -1 && snakeDirection.x != 1) {
							snakeDirection.set(-1, 0);
						}
						break;
					case -4:
					case 54:
						if (snakeDirection.x != -1 && snakeDirection.x != 1) {
							snakeDirection.set(1, 0);
						}
						break;
				}
			}
		}
	}

	public void placeFood() {
		boolean stop = false;
		while (!stop) {
			food.set(random.nextInt(row - 1), random.nextInt(column - 1));
			boolean ok = true;
			for (int i = 1; i < snake.size(); i++) {
				Translate part = (Translate) snake.elementAt(i);
				if (isCollide(food, part)) {
					ok = false;
					break;
				}
			}
			if (ok) {
				stop = true;
			}
		}
	}

	// NOTE: No need delta time
	public void update() {
		// Snake movement
		Translate snakeOldHead = (Translate) snake.firstElement();
		Translate snakeNewHead = (Translate) snake.pop();
		snakeNewHead.set(
			(row + snakeOldHead.x + snakeDirection.x) % row,
			(column + snakeOldHead.y + snakeDirection.y) % column
		);
		snake.insertElementAt(snakeNewHead, 0);

		// Snake eat food
		if (isCollide(snakeNewHead, food)) {
			snakeNewHead = new Translate(
				(row + snakeNewHead.x + snakeDirection.x) % row,
				(column + snakeNewHead.y + snakeDirection.y) % column
			);
			snake.insertElementAt(snakeNewHead, 0);
			placeFood();
		}

		// Check if snake collide with own body
		for (int i = 1; i < snake.size(); i++) {
			Translate part = (Translate) snake.elementAt(i);
			if (isCollide(snakeNewHead, part)) {
				restart();
			}
		}
	}

	public void run() {
		while (isThreadRunning) {
			try {
				if (isUpdate) {
					update();
				}
				repaint();
				float fps = 6;
				Thread.sleep((long) (1000.0 / fps));
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean isCollide(Translate a, Translate b) {
		return (a.x == b.x && a.y == b.y);
	}
}
