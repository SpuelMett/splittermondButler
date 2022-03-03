package View;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import Controller.Controller;
import Model.Roll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class DiscordView extends ListenerAdapter implements View {

	private Controller cntrl;
	private GuildMessageReceivedEvent currentEvent;
	
	
	public DiscordView(Controller contrl) {
		this.cntrl = contrl;
		
		try {
			this.initateBot();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * initiates the bot, handles login and displays parameters (currently playing...)
	 * @throws LoginException
	 */
	private void initateBot() throws LoginException {
		JDABuilder jda = JDABuilder.createDefault("ODYzMTQ4NjgxNjQ4Nzk5NzY1.YOir2g.cFTr9hgwljOHBBQvnqC4g478w_k");
		jda.setActivity(Activity.playing("Throwing Dice left, right and center"));
		jda.setStatus(OnlineStatus.ONLINE);
		jda.addEventListeners(this);
		
		jda.setChunkingFilter(ChunkingFilter.ALL);
		jda.setMemberCachePolicy(MemberCachePolicy.ALL);
		jda.enableIntents(GatewayIntent.GUILD_MEMBERS);
		
		jda.build();
		
	}
		
	/**
	 * required implementation of the listener
	 */
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		this.currentEvent = event;
		String args = event.getMessage().getContentRaw();
		System.out.println("Listener Fired");
		System.out.println(this.currentEvent.getMessage().getContentRaw());
		
		//dont react if the bot send the message!
		if(!event.getAuthor().isBot())
			
			//only react if the correct prefix is used
			if(event.getMessage().getContentRaw().startsWith("!"))
				this.cntrl.executeCommand(args);
	}

	@Override
	public GuildMessageReceivedEvent getCurrentEvent() {
		return currentEvent;
	}
	
	@Override
	public void setCurrentEvent(GuildMessageReceivedEvent currentEvent) {
		this.currentEvent = currentEvent;
	}

	@Override
	public void displayMsg(String msg) {
		currentEvent.getChannel().sendMessage(msg).queue();
		
	}
	
	@Override
	public void reply(String msg) {
		 currentEvent.getMessage().reply(msg).queue();
		
	}

	@Override
	public void displayRoll(Roll rollEvent) {
		//private void DiceRoll(GuildMessageReceivedEvent event, int diceAmount, int diceSize) {
//			
//			 //Roll rollEvent = new Roll(diceAmount, diceSize);
//			 int[] results = rollEvent.getResultField();
//			
//			 int[] throwParam = new int[2];
//			 	   throwParam[0]= rollEvent.getDiceAmount();
//			 	   throwParam[1]= rollEvent.getDiceSize();
//			 
			 	   
			 RollTemplate rollTemplate= new RollTemplate();
			 EmbedBuilder embed = rollTemplate.buildRollEmbed(this.currentEvent, rollEvent);
			 
			 currentEvent.getMessage().reply(embed.build()).queue();
			 embed.clear();
		 }
		
	

	@Override
	public void displayError(Exception exc) {
		currentEvent.getChannel().sendMessage(exc.getLocalizedMessage()).queue();
		
	}

	@Override
	public void displayTickNew() {
		this.displayMsg("-------------------Neue Tickleiste-----------------------");
		
	}


	@Override
	public void displayTickContent(int currentTick, User[] turn, ArrayList<ArrayList<String>> nextMoves) {
		TickTemplate tickTemplate = new TickTemplate();
		EmbedBuilder embed = tickTemplate.buildTickEmbed(this.currentEvent, currentTick, turn, nextMoves);
		 currentEvent.getChannel().sendMessage(embed.build()).queue();
		 embed.clear();
		
	}

	@Override
	public void displayTickStart(User[] playerNames, Integer[] playerPos) {
		TickTemplate tickTemplate = new TickTemplate();
		EmbedBuilder embed = tickTemplate.buildStartingEmbed(this.currentEvent, playerNames, playerPos);
		 currentEvent.getChannel().sendMessage(embed.build()).queue();
		 embed.clear();
		
	}

}