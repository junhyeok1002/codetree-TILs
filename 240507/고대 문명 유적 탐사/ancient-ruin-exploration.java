import java.io.*;
import java.util.*;


public class Main {
	static int[][] delta = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
	
	static int N, M;
	static int[][] map;
	static Queue<Integer> blocks;
	static PriorityQueue<Node> deletePQ;
	
	public static void main(String[] args) throws Exception{
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		StringBuilder sb = new StringBuilder();
		
		String[] line = bf.readLine().split(" ");
		N = Integer.parseInt(line[0]);
		M = Integer.parseInt(line[1]);
		blocks = new LinkedList<>();
		deletePQ = new PriorityQueue<>();
		map = new int[5][5];
		
		for(int i =0 ; i < 5; i++) {
			line = bf.readLine().split(" ");
			for(int j = 0; j < 5; j++) {
				map[i][j] = Integer.parseInt(line[j]);
			}
		}
		
		line = bf.readLine().split(" ");
		for(int j = 0; j < M; j++) {
			blocks.offer(Integer.parseInt(line[j]));
		}
		
		
//		Print();
//		
//		rotate90(2,2);
//		System.out.println(checkScore(false));
//		
//		
//		Print();

		
		Turn :
		for(int n= 1; n <= N; n++) {
//			System.out.println("초기 상태 ");
//			Print();
			
			
			int turnScore = 0;
			
			// 회전하고 채우기 => 회전 시 점수가 없으면 그 즉시 종
			int rotateScore = selectBestRotation();
			
			if(rotateScore == 0) {
				break Turn;
			}
			
//			System.out.println("회전  후 ");
//			Print();
			
			turnScore += rotateScore;
			fillDelete();
			
//			System.out.println("채운  후  ");
//			Print();
			
			// 다시 점수를 챙겼을 때 점수가 없을 때까지 반복
			
			int addScore = checkScore(true);
			fillDelete();
			
			while(addScore > 0) {
				turnScore += addScore;
	
				addScore = checkScore(true);
				fillDelete();
			}
			
			sb.append(turnScore + " ");
		}
		

		System.out.println(sb);
		
	}
	
	
	private static int selectBestRotation() {
		int bestScore = 0, bestRotateCnt = 4, best_i =0, best_j = 0;
		
		for(int j = 1; j < 4; j++) {
			for(int i = 1; i < 4; i++) {
				int[] result = rotation(i,j);
				
				if(bestScore < result[0]) {
					bestScore = result[0];
					bestRotateCnt = result[1];
					best_i = i;
					best_j = j;
				}
				
				else if(bestScore == result[0] && bestRotateCnt > result[1]) {
					bestScore = result[0];
					bestRotateCnt = result[1];
					best_i = i;
					best_j = j;
				}
			}
		}
		
		// 최고의 회전수가 정해졌으니 진짜 회전하
		for(int r = 1; r <= bestRotateCnt; r++) {
			rotate90(best_i, best_j);
		}
		
		checkScore(true);
		
		return bestScore;
	}
	
	private static int[] rotation(int std_i, int std_j) {
		int[][] tempMap = new int[5][5];
		for(int i =0 ; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				tempMap[i][j] = map[i][j];
			}
		}
		
		int bestScore = 0;
		int bestRotateCnt = 0;
		
		for(int r = 1; r <= 3; r++) {
			rotate90(std_i, std_j);
			int score = checkScore(false);
			if(score > bestScore) {
				bestScore = score;
				bestRotateCnt = r;
			}
		}
		
		
		for(int i =0 ; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				map[i][j] = tempMap[i][j];
			}
		}
		
		return new int[] {bestScore, bestRotateCnt};
	}
	
	private static void rotate90(int std_i, int std_j) {
		int[][] temp = new int[3][3];
		std_i--; std_j--;
		
		for(int i = std_i; i < std_i + 3; i++) {
			for(int j = std_j; j < std_j + 3; j++) {
				temp[j- std_j][2-(i-std_i)] = map[i][j];
			}
		}
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				map[i+std_i][j+std_j] = temp[i][j];
			}
		}
		
		
	}
	
	private static void fillDelete() {
		while(!deletePQ.isEmpty()) {
			Node node = deletePQ.poll();
			map[node.i][node.j] = blocks.poll();
		}
	}
	
	private static int checkScore(boolean isPQ) {
		int score = 0;
		boolean[][] visited = new boolean[5][5];
		
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				// 방문했으면 넘어
				if(visited[i][j]) continue;
				
				Node newNode = new Node(i, j, map[i][j]);
				Queue<Node> scoreList = BFS(newNode, visited);
				
				if(scoreList.size() >= 3) {
					score += scoreList.size();
					
					if(isPQ) {
						while(!scoreList.isEmpty()) {
							deletePQ.offer(scoreList.poll());
						}
					}
					
				}
			}
		}

		return score;
	}
	
	private static Queue<Node> BFS(Node startNode, boolean[][] visited) {
		Queue<Node> queue = new LinkedList<>();
		Queue<Node> score = new LinkedList<>();
		
		visited[startNode.i][startNode.j] = true;
		queue.offer(startNode);
		score.offer(startNode);
		
		while(!queue.isEmpty()) {
			Node node = queue.poll();
			
			for(int k = 0; k < 4; k++) {
				int nr = node.i + delta[k][0];
				int nc = node.j + delta[k][1];
				
				// 범위 안에 있고 나랑 같으면서 방문하지 않으면 가
				if(0 <= nr && nr < 5 && 0 <= nc && nc < 5 &&
					!visited[nr][nc] && (map[nr][nc] == map[node.i][node.j])){
					
					Node newNode = new Node(nr, nc, map[nr][nc]);
					visited[nr][nc] = true;
					queue.offer(newNode);
					score.offer(newNode);
						
				}
			}
		}
		
		return score;
	}
	
	
	
	private static void Print() { 
		for(int i =0 ; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println(blocks.toString());
		
	}
	
}
class Node<T> implements Comparable<T>{
	int i;
	int j;
	int num;
	
	public Node(int i, int j, int num) {
		super();
		this.i = i;
		this.j = j;
		this.num = num;
	}

	@Override
	public String toString() {
		return "Node [i=" + i + ", j=" + j + ", num=" + num + "]";
	}

	@Override
	public int compareTo(T o) {
		Node compare = (Node) o;
		
		if(this.j > compare.j) return 1;
		if(this.j < compare.j) return -1;
		
		if(this.i < compare.i) return 1;
		if(this.i > compare.i) return -1;
		
		return 0;
	}
}