package dev.nikipro50.antibuild;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public final class AntiBuild extends JavaPlugin implements Listener, CommandExecutor {
    File a;
    YamlConfiguration b;
    List<String> c;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("Starting...");
        this.c = Lists.newArrayList();
        a();
        b();
        d();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginCommand("build").setExecutor(this);
        Bukkit.getPluginCommand("build").setTabCompleter(this);
        Bukkit.getConsoleSender().sendMessage("AntiBuild started!");
    }

    @Override
    public void onDisable() {
        c();
    }

    @EventHandler
    void a(BlockBreakEvent event) {
        if (this.c.contains(event.getPlayer().getUniqueId().toString()))
            return;
        event.setCancelled(true);
    }

    public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
        if (!(a instanceof Player)) return true;
        if (b.getLabel().equalsIgnoreCase("build")) {
            if (!a.hasPermission("build.use")) {
                a.sendMessage(Lang.C.c());
                return true;
            }
            Player e = (Player) a;
            if (this.c.contains(e.getUniqueId().toString())) {
                a(e);
                e.sendMessage(Lang.B.c());
                return true;
            }
            a(e);
            e.sendMessage(Lang.A.c());
        }
        return false;
    }

    void a() {
        this.a = new File(getDataFolder(), "players.yml");
        if (!this.a.exists())
            try {
                this.a.getParentFile().mkdir();
                this.a.createNewFile();
                this.b = YamlConfiguration.loadConfiguration(this.a);
                this.b.set("Players", Lists.newArrayList());
                this.b.save(this.a);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        this.b = YamlConfiguration.loadConfiguration(this.a);
    }

    void a(Player a) {
        if (this.c.contains(a.getUniqueId().toString())) {
            this.c.remove(a.getUniqueId().toString());
            return;
        }
        this.c.add(a.getUniqueId().toString());
    }

    void b() {
        if (this.c == null) c = Lists.newArrayList();
        this.c.addAll(this.b.getStringList("Players"));
    }

    void c() {
        List<String> a = Lists.newArrayList();
        a.addAll(this.c);
        this.b.set("Players", a);
    }

    void d() {
        File a = new File(getDataFolder(), "lang.yml");
        if (!a.exists()) {
            try {
                a.getParentFile().mkdirs();
                a.createNewFile();
                InputStream b = this.getResource("lang.yml");
                if (b != null) {
                    InputStreamReader c = new InputStreamReader(b);
                    YamlConfiguration d = new YamlConfiguration();
                    try {
                        d.load(c);
                        d.save(a);
                        Lang.a(d);
                    } catch (InvalidConfigurationException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            } catch (IOException e) {
                Bukkit.getPluginManager().disablePlugin(this);
                this.setEnabled(false);
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration b = YamlConfiguration.loadConfiguration(a);
        for (Lang c : Lang.values()) {
            if (b.getString(c.a()) == null)
                b.set(c.a(), c.b());
        }
        Lang.a(b);
        try {
            b.save(a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    enum Lang {
        A("on", "&9AntiBuild &8• &7Build mode &a&nEnabled&7."),
        B("off", "&9AntiBuild &8• &7Build mode &c&nDisabled&7."),
        C("NoPerms", "&cYou don't have permission to do that!");

        final String a;
        final String b;
        static YamlConfiguration c;

        Lang(String a, String b) {
            this.a = a;
            this.b = b;
        }

        static void a(YamlConfiguration a) {
            c = a;
        }

        String a() {
            return this.a;
        }

        String b() {
            return this.b;
        }

        String c() {
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(c.getString(this.a, this.b)));
        }
    }
}
