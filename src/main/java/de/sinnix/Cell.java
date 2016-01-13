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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author "Florian Miess"
 * @date 12.01.2016
 *
 */
public class Cell {

	/**
	 * Zellennachbarn: 
	 * 0|1|2 
	 * 3|X|5 
	 * 6|7|8
	 */
	@JsonIgnore private Cell	top_left;		// 0
	@JsonIgnore private Cell	top;			// 1
	@JsonIgnore private Cell	top_right;		// 2
	@JsonIgnore private Cell	left;			// 3
	@JsonIgnore private Cell	right;			// 5
	@JsonIgnore private Cell	bottom_left;	// 6
	@JsonIgnore private Cell	bottom;			// 7
	@JsonIgnore private Cell	bottom_right;	// 8
	
	private int x;
	private int y;
	private boolean alive;
	private boolean nextAlive;
	
	Cell(Boolean alive, int x, int y) {
		this.alive = alive;
		this.x = x;
		this.y = y;
	}

	public void calcNextGeneration() {
		int livingNeighbours = countLivingNeighbours(); 
		if (this.alive) {
			switch (livingNeighbours) {
			case 0:
			case 1:
				// sterbe an Einsamkeit
				this.nextAlive = false;
				break;
			case 2:
			case 3:
				// bleibe am Leben
				this.nextAlive = true;
				break;
			default:
				// sterbe an Überbevölkerung
				this.nextAlive = false;
			}
		}
		else {
			// werde neu geboren
			if (livingNeighbours == 3) this.nextAlive = true; 
		}
	}
	
	public void setNextGeneration() {
		this.alive = this.nextAlive;
	}
	
	// nicht initialisierte Zellen - z.B. an den Rändern werden als hier als tot gewertet
	private int countLivingNeighbours() {
		int count = 0;
		if (top_left != null && top_left.isAlive()) count++;
		if (top != null && top.isAlive()) count++;
		if (top_right != null && top_right.isAlive()) count++;
		if (left != null && left.isAlive()) count++;
		if (right != null && right.isAlive()) count++;
		if (bottom_left != null && bottom_left.isAlive()) count++;
		if (bottom != null && bottom.isAlive()) count++;
		if (bottom_right != null && bottom_right.isAlive()) count++;
		return count;
	}

	public Cell getTop_left() {
		return top_left;
	}

	public void setTop_left(Cell top_left) {
		this.top_left = top_left;
	}

	public Cell getTop() {
		return top;
	}

	public void setTop(Cell top) {
		this.top = top;
	}

	public Cell getTop_right() {
		return top_right;
	}

	public void setTop_right(Cell top_right) {
		this.top_right = top_right;
	}

	public Cell getLeft() {
		return left;
	}

	public void setLeft(Cell left) {
		this.left = left;
	}

	public Cell getRight() {
		return right;
	}

	public void setRight(Cell right) {
		this.right = right;
	}

	public Cell getBottom_left() {
		return bottom_left;
	}

	public void setBottom_left(Cell bottom_left) {
		this.bottom_left = bottom_left;
	}

	public Cell getBottom() {
		return bottom;
	}

	public void setBottom(Cell bottom) {
		this.bottom = bottom;
	}

	public Cell getBottom_right() {
		return bottom_right;
	}

	public void setBottom_right(Cell bottom_right) {
		this.bottom_right = bottom_right;
	}

	public Boolean isAlive() {
		return alive;
	}

	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public String toString() {
		return isAlive() ? "O" : " ";
	}
	
}
