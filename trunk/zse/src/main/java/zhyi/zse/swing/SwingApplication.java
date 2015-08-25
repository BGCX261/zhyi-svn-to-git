/*
 * Copyright (C) 2011 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zse.swing;

import java.util.ServiceLoader;
import javax.swing.SwingUtilities;

/**
 * An SPI that acts as a common entrance of Swing applications.
 * <p>Besides implementing {@link #bootstrap(String[])} method, a subclass should
 * also be configured properly following the {@link ServiceLoader} pattern, and
 * the application needs to register this class as the main class.</p>
 * <p>If an implementation is found, the system look and feel is set, and then
 * {@link #bootstrap(String[])} method is invoked on the EDT.</p>
 * <p>Only the first found implementation is used. An exception is thrown if no
 * implementation is found.</p>
 * @author Zhao Yi
 */
public abstract class SwingApplication {
    /**
     * Starts up the application. This method is running on the EDT.
     * @param args The command line arguments.
     */
    protected abstract void bootstrap(String[] args);

    public static void main(final String[] args) {
        final SwingApplication app = ServiceLoader.load(
                SwingApplication.class).iterator().next();
        SwingHelper.initSystemlookAndFeel();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.bootstrap(args);
            }
        });
    }
}
