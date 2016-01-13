/** Copyright (c) 2015, innoQ Deutschland GmbH
 *  All rights reserved.
 *   
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the innoQ Deutschland GmbH nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.sinnix;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author "Florian Miess"
 * @date 12.01.2016
 *
 */
@ServerEndpoint("/gof")
public class GoFServer {
	
	
	private Session						session;
	private ScheduledExecutorService	executor	= Executors.newScheduledThreadPool(2);
	private Court						court;
	

	@OnOpen
	public void startWebsocket(Session session) {
		this.session = session;
		
		Logger.getAnonymousLogger().info("GameOfLife-Server connected");
		initializeGame(10,10);
		session.getAsyncRemote().sendObject(court.getJsonFields());
	}
	
	@OnClose
	public void stopWebsocket(Session session) {
		Logger.getAnonymousLogger().info("Websocket geschlossen");
		
		executor.shutdown();
	}

	@OnMessage
	public void receiveMessage(String message) throws IOException {
		Logger.getAnonymousLogger().info("Server received Message '" + message + "'");
		
		if (session != null && session.isOpen()) {
			
			ObjectMapper mapper = new ObjectMapper();
			SocketMessage msg = mapper.readValue(message, SocketMessage.class);
			
			switch (msg.getAction()) {
			case initialize:
				initializeGame(msg.getHeight(), msg.getWidth());
				break;
			case start:
				int speed = msg.getSpeed();
				if (!executor.isShutdown()) {
					executor.shutdown();
				}
				startGame(1000 / speed);
				break;
			case stop:
				Logger.getAnonymousLogger().info("stoppe Spiel");
				executor.shutdown();
				break;
			default:
				break;
			
			}
		}
	}
	
	private void initializeGame(int heigth, int width) {
		Logger.getAnonymousLogger().info("initialisiere Spiel");
		court = new Court(heigth, width);
		court.initializeFields();
		
		Logger.getAnonymousLogger().info("Spielfeld: \n" + court);
	}
	
	private void startGame(int delay) {
		Logger.getAnonymousLogger().info("starte Spiel (delay=" + delay +")");
		if (executor.isShutdown()) {
			executor = Executors.newScheduledThreadPool(2);
		}
		
		// Spiel berechnen und abschicken
		executor.scheduleWithFixedDelay(
				()->{
					court.calculateNextGeneration();
					//Logger.getAnonymousLogger().info("Zeit: " + LocalDateTime.now());
					session.getAsyncRemote().sendObject(court.getJsonFields());
					}, 
				0, 
				delay, 
				TimeUnit.MILLISECONDS);
	}

}
