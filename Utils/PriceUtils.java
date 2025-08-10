/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

/**
 *
 * @author ASUS
 */
import java.text.DecimalFormat;

public class PriceUtils {
    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public static String formatCurrency(double amount) {
        return formatter.format(amount) + " VND";
    }
}
