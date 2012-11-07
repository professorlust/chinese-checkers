package org.scoutant.cc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO switch to platform Log when tests done...

/**
 * Players in this order :
 *<p>    3 
 *<p> 2     4
 *<p> 1     5
 *<p>    0
 *
 *<p>Directions in the board : 
 *<p>  0  1
 *<p> 5    2
 *<p>  4  3
 *
 */
public class AI {
	private static String tag = "ai";
	// Consider as first directions pointing to opposite triangle.
	public static final int[][] dirs = {
		{0, 1, 5, 2, 4, 3},  
		{1, 2, 0, 3, 5, 4}, // 1  
		{2, 3, 1, 4, 0, 5}, // 2 
		{3, 4, 2, 5, 1, 0}, // 3 
		{4, 5, 3, 0, 2, 1}, // 4 
		{5, 0, 4, 1, 3, 2}, // 5 
	};

	private Game game;
	private Board board;
	private Board track;
	private List<Move> moves = new ArrayList<Move>();;
	public AI(Game game) {
		this.game = game;
		board = game.board;
		track = new Board();
	}

	public Move think(int color, int level) {
		List<Move> moves = thinkUpToNJumps(color, level);

		if (moves.size()<=8) {
			// let's consider hops too
			thinkHops(color, level);
			Collections.sort(moves, MoveComparator.comparators[color]);
		}
		if (moves.size()==0) {
			// TODO endgame
			Log.d(tag, "no more moves... for player : " + color);
			return null;
		}
		// TODO random move among the 10 best ones...
		Move move = moves.get(0);
		return move ;
	}

	// TODO consider hops only at least
	protected void thinkHops(int color, int level) {
		Player player = game.player(color);
		for (Peg peg : player.pegs()) {
			Log.d(tag, "**** hops ?");
			// consider only positive hops
			for (int i=0; i<2; i++) {
				int dir = dirs[color][i];
				Point p = board.hop(peg.point, dir);
				if (p!=null && !board.is(p)) {
					// target is a hole not occupied by a peg
					Move move = new Move( peg.point);
					move.add(p);
					moves.add( move);
					Log.d(tag, "can hop : " + move);
				}
			}
		}
	}
	
	/**
	 * @return the list of moves for given play. Considering only jumps.
	 */
	// TODO reactor removing method return
	protected List<Move> thinkUpToNJumps(int color, int level) {
		// TODO level
		track = new Board();
		moves.clear();
		Player player = game.player(color);
		for (Peg peg : player.pegs()) {
			Log.d(tag, "*********************************************************************************");
			Log.d(tag, "peg : " + peg );
			Log.d(tag, "*********************************************************************************");
			Move move = new Move(peg.point);
			visite( color, move);
		}
		Collections.sort(moves, MoveComparator.comparators[color]);
		Log.d(tag, "# of jumps : " + moves.size());
		return moves;
	}
	
	private void visite(int color, Move move) {
		for (int dir:dirs[color] ) {
			Log.d(tag, "** dir : " + dir);
			visite(color, move, dir);
		}
	}
	
	private void visite(int color, Move move, int dir) {
		Point p = board.jump(move.last(), dir);
//		Log.d(tag, "dir : " + dir +", jump to : " + p);
		if (p==null) return;
		if (track.is(p)) {
			Log.d(tag, "already visited point " + p);
			return;
		}
		track.set(p);
		Move found = move.clone();
		found.add(p);
//		if (found.lenght( color)>0) {
		// TODO many if considering zero lenght move even in middle game?
		if (found.lenght( color)>=0) {
			Log.d(tag, "move ! [ " + found.lenght(color) + " ] "+ found);
			moves.add(found);
		} 
		Log.d(tag, "+++++++++++++++++++++++++++++++++++++++++++");
		visite( color, found.clone());
		Log.d(tag, "-------------------------------------------");
	}
}
