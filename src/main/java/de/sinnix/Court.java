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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author "Florian Miess"
 * @date 12.01.2016
 *
 */
public class Court {
	private int	height	= 10;
	private int	width	= 10;

	private List<List<Cell>> fields;

	public Court() {
	}
	
	public Court(int heigth, int width) {
		this.width = width;
		this.height = heigth;
	}
	
	public List<List<Cell>> getFields() {
		return fields;
	}
	
	public String getJsonFields() {
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(fields);
		}
		catch (JsonProcessingException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
		}
		
		return json;
	}

	public void initializeFields() {
		fields = new ArrayList<>(height);
		for (int y = 0; y < height; y++) {
			
			List<Cell> row = new ArrayList<>(width);
			for (int x = 0; x < width; x++) {
				Cell c = new Cell(Math.random() <= 0.5d ? false : true, x, y);
				row.add(c);
			}
			fields.add(row);
		}
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				initializeCell(fields.get(y).get(x), y, x);
			}
		}
	}
	
	/**
	 * Geschwindigkeitskritisch
	 */
	public void calculateNextGeneration() {
		// berechne nächste Generation
		fields.parallelStream().forEach(y -> 
			y.parallelStream().forEach(x -> 
				x.calcNextGeneration()));
		
		
		// schreibe Ergebnisse in die Zellen
		fields.parallelStream().forEach(y -> 
		y.parallelStream().forEach(x -> 
			x.setNextGeneration()));
	}
	
	public boolean isStillActive() {
		boolean result = false;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (fields.get(y).get(x).isAlive()) {
					result = true;
					break;
				}
				
			}
		}
		return result;
	}

	private void initializeCell(Cell c, int y, int x) {
		if (y > 0) {
			if (x > 0) c.setTop_left( fields.get(y-1).get(x-1) );
			c.setTop( fields.get(y-1).get(x) );
			if (x < width -1) c.setTop_right( fields.get(y-1).get(x+1));
		}
		if (x > 0) c.setLeft( fields.get(y).get(x-1) );
		if (x < width - 1) c.setRight(fields.get(y).get(x+1));
		if (y < height -1) {
			if (x > 0) c.setBottom_left( fields.get(y+1).get(x-1) );
			c.setBottom( fields.get(y+1).get(x) );
			if (x < width -1) c.setBottom_right( fields.get(y+1).get(x+1));
		}
	}
	
	public String toString() {
		String answer = "";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				answer += fields.get(y).get(x).toString();
			}
			answer += "\n";
		}
		
		return answer;
	}
}
