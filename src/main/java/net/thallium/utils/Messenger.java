package net.thallium.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class Messenger {
    /*
     messsage: "desc me ssa ge"
     desc contains:
     i = italic
     s = strikethrough
     u = underline
     b = bold
     o = obfuscated

     w = white
     y = yellow
     m = magenta (light purple)
     r = red
     c = cyan (aqua)
     l = lime (green)
     t = light blue (blue)
     f = dark gray
     g = gray
     d = gold
     p = dark purple (purple)
     n = dark red (brown)
     q = dark aqua
     e = dark green
     v = dark blue (navy)
     k = black

     / = action added to the previous component
     */

	private static ITextComponent _applyStyleToTextComponent(ITextComponent comp, String style) {
		//could be rewritten to be more efficient
		comp.getStyle().setItalic(style.indexOf('i') >= 0);
		comp.getStyle().setStrikethrough(style.indexOf('s') >= 0);
		comp.getStyle().setUnderlined(style.indexOf('u') >= 0);
		comp.getStyle().setBold(style.indexOf('b') >= 0);
		comp.getStyle().setObfuscated(style.indexOf('o') >= 0);
		comp.getStyle().setColor(TextFormatting.WHITE);
		if (style.indexOf('w') >= 0)
			comp.getStyle().setColor(TextFormatting.WHITE); // not needed
		if (style.indexOf('y') >= 0)
			comp.getStyle().setColor(TextFormatting.YELLOW);
		if (style.indexOf('m') >= 0)
			comp.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
		if (style.indexOf('r') >= 0)
			comp.getStyle().setColor(TextFormatting.RED);
		if (style.indexOf('c') >= 0)
			comp.getStyle().setColor(TextFormatting.AQUA);
		if (style.indexOf('l') >= 0)
			comp.getStyle().setColor(TextFormatting.GREEN);
		if (style.indexOf('t') >= 0)
			comp.getStyle().setColor(TextFormatting.BLUE);
		if (style.indexOf('f') >= 0)
			comp.getStyle().setColor(TextFormatting.DARK_GRAY);
		if (style.indexOf('g') >= 0)
			comp.getStyle().setColor(TextFormatting.GRAY);
		if (style.indexOf('d') >= 0)
			comp.getStyle().setColor(TextFormatting.GOLD);
		if (style.indexOf('p') >= 0)
			comp.getStyle().setColor(TextFormatting.DARK_PURPLE);
		if (style.indexOf('n') >= 0)
			comp.getStyle().setColor(TextFormatting.DARK_RED);
		if (style.indexOf('q') >= 0)
			comp.getStyle().setColor(TextFormatting.DARK_AQUA);
		if (style.indexOf('e') >= 0)
			comp.getStyle().setColor(TextFormatting.DARK_GREEN);
		if (style.indexOf('v') >= 0)
			comp.getStyle().setColor(TextFormatting.DARK_BLUE);
		if (style.indexOf('k') >= 0)
			comp.getStyle().setColor(TextFormatting.BLACK);
		return comp;
	}

	static String heatmap_color(double actual, double reference) {
		String color = "e";
		if (actual > 0.5D * reference)
			color = "y";
		if (actual > 0.8D * reference)
			color = "r";
		if (actual > reference)
			color = "m";
		return color;
	}

	static String creatureTypeColor(EnumCreatureType type) {
		switch (type) {
			case MONSTER:
				return "n";
			case CREATURE:
				return "e";
			case AMBIENT:
				return "f";
			case WATER_CREATURE:
				return "v";
		}
		return "w";
	}

	private static ITextComponent _getChatComponentFromDesc(String message, ITextComponent previous_message) {
		String[] parts = message.split("\\s", 2);
		String desc = parts[0];
		String str = "";
		if (parts.length > 1)
			str = parts[1];
		if (desc.charAt(0) == '/') // deprecated
		{
			if (previous_message != null)
				previous_message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message));
			return previous_message;
		}
		if (desc.charAt(0) == '?') {
			if (previous_message != null)
				previous_message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message.substring(1)));
			return previous_message;
		}
		if (desc.charAt(0) == '!') {
			if (previous_message != null)
				previous_message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, message.substring(1)));
			return previous_message;
		}
		if (desc.charAt(0) == '^') {
			if (previous_message != null)
				previous_message.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Messenger.m(null, message.substring(1))));
			return previous_message;
		}
		ITextComponent txt = new TextComponentString(str);
		return _applyStyleToTextComponent(txt, desc);
	}

	/*
	builds single line, multicomponent message, optionally returns it to sender, and returns as one chat messagge
	 */
	public static ITextComponent m(ICommandSender receiver, Object... fields) {
		ITextComponent message = new TextComponentString("");
		ITextComponent previous_component = null;
		for (Object o : fields) {
			if (o instanceof ITextComponent) {
				message.appendSibling((ITextComponent) o);
				previous_component = (ITextComponent) o;
				continue;
			}
			String txt = o.toString();
			ITextComponent comp = _getChatComponentFromDesc(txt, previous_component);
			if (comp != previous_component)
				message.appendSibling(comp);
			previous_component = comp;
		}
		if (receiver != null)
			receiver.sendMessage(message);
		return message;
	}

	static void print_server_message(MinecraftServer server, String message) {
		server.sendMessage(new TextComponentString(message));
		ITextComponent txt = m(null, "gi " + message);
		for (EntityPlayer entityplayer : server.getPlayerList().getPlayers()) {
			entityplayer.sendMessage(txt);
		}
	}
}
