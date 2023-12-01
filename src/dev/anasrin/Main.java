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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

/**
 * @author Anas Rin <mynameanasrar@gmail.com>
 */
public class Main extends MIDlet implements CommandListener {

	private Display display;
	DrawCanvas canvas = null;
	Thread threadCanvas = null;

	public Main() {
	}

	public void startApp() {
		display = Display.getDisplay(this);

		canvas = new DrawCanvas();
		canvas.addCommand(new Command("Exit", Command.EXIT, 1));
		canvas.setCommandListener(this);

		display.setCurrent(canvas);

		threadCanvas = new Thread(canvas);
		threadCanvas.start();
	}

	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
	}

	public void commandAction(Command c, Displayable d) {
		int command = c.getCommandType();

		switch (command) {
			case Command.EXIT:
				this.canvas.isThreadRunning = false;
				System.gc();
				this.notifyDestroyed();
				break;
			default:
				System.err.println("NOT IMPLEMENTED: COMMAND " + String.valueOf(command));
				break;
		}
	}
}
