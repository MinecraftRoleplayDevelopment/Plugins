package net.milkbowl.vault.economy;

import java.util.List;
import org.bukkit.OfflinePlayer;

public abstract interface Economy
{
  public abstract boolean isEnabled();

  public abstract String getName();

  public abstract boolean hasBankSupport();

  public abstract int fractionalDigits();

  public abstract String format(double paramDouble);

  public abstract String currencyNamePlural();

  public abstract String currencyNameSingular();

  @Deprecated
  public abstract boolean hasAccount(String paramString);

  public abstract boolean hasAccount(OfflinePlayer paramOfflinePlayer);

  @Deprecated
  public abstract boolean hasAccount(String paramString1, String paramString2);

  public abstract boolean hasAccount(OfflinePlayer paramOfflinePlayer, String paramString);

  @Deprecated
  public abstract double getBalance(String paramString);

  public abstract double getBalance(OfflinePlayer paramOfflinePlayer);

  @Deprecated
  public abstract double getBalance(String paramString1, String paramString2);

  public abstract double getBalance(OfflinePlayer paramOfflinePlayer, String paramString);

  @Deprecated
  public abstract boolean has(String paramString, double paramDouble);

  public abstract boolean has(OfflinePlayer paramOfflinePlayer, double paramDouble);

  @Deprecated
  public abstract boolean has(String paramString1, String paramString2, double paramDouble);

  public abstract boolean has(OfflinePlayer paramOfflinePlayer, String paramString, double paramDouble);

  @Deprecated
  public abstract EconomyResponse withdrawPlayer(String paramString, double paramDouble);

  public abstract EconomyResponse withdrawPlayer(OfflinePlayer paramOfflinePlayer, double paramDouble);

  @Deprecated
  public abstract EconomyResponse withdrawPlayer(String paramString1, String paramString2, double paramDouble);

  public abstract EconomyResponse withdrawPlayer(OfflinePlayer paramOfflinePlayer, String paramString, double paramDouble);

  @Deprecated
  public abstract EconomyResponse depositPlayer(String paramString, double paramDouble);

  public abstract EconomyResponse depositPlayer(OfflinePlayer paramOfflinePlayer, double paramDouble);

  @Deprecated
  public abstract EconomyResponse depositPlayer(String paramString1, String paramString2, double paramDouble);

  public abstract EconomyResponse depositPlayer(OfflinePlayer paramOfflinePlayer, String paramString, double paramDouble);

  @Deprecated
  public abstract EconomyResponse createBank(String paramString1, String paramString2);

  public abstract EconomyResponse createBank(String paramString, OfflinePlayer paramOfflinePlayer);

  public abstract EconomyResponse deleteBank(String paramString);

  public abstract EconomyResponse bankBalance(String paramString);

  public abstract EconomyResponse bankHas(String paramString, double paramDouble);

  public abstract EconomyResponse bankWithdraw(String paramString, double paramDouble);

  public abstract EconomyResponse bankDeposit(String paramString, double paramDouble);

  @Deprecated
  public abstract EconomyResponse isBankOwner(String paramString1, String paramString2);

  public abstract EconomyResponse isBankOwner(String paramString, OfflinePlayer paramOfflinePlayer);

  @Deprecated
  public abstract EconomyResponse isBankMember(String paramString1, String paramString2);

  public abstract EconomyResponse isBankMember(String paramString, OfflinePlayer paramOfflinePlayer);

  public abstract List<String> getBanks();

  @Deprecated
  public abstract boolean createPlayerAccount(String paramString);

  public abstract boolean createPlayerAccount(OfflinePlayer paramOfflinePlayer);

  @Deprecated
  public abstract boolean createPlayerAccount(String paramString1, String paramString2);

  public abstract boolean createPlayerAccount(OfflinePlayer paramOfflinePlayer, String paramString);
}

/* Location:           D:\Github\Mechanics\Vault.jar
 * Qualified Name:     net.milkbowl.vault.economy.Economy
 * JD-Core Version:    0.6.2
 */