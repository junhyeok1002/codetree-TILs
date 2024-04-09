import java.util.*;
import java.io.*;

public class Main {
	static int N, M, K;
	static Tower[][] map;
	static Tower attack, defence;
	static int[][] delta = {{0,1},{1,0},{0,-1},{-1,0}};
	static int[][] delta8 = {{0,1},{1,0},{0,-1},{-1,0},{1,1},{1,-1},{-1,-1},{-1,1}};
	static boolean[][] dfs_visited;
	static boolean[][] attacked = new boolean[N][M];
	static Queue<Node> queue = new LinkedList<>();
	
	public static void main(String[] args) throws Exception {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		
		// N, M, K 입력
		String[] line = bf.readLine().split(" ");
		N = Integer.parseInt(line[0]);
		M = Integer.parseInt(line[1]);
		K = Integer.parseInt(line[2]);
		
		// map 배열 생성 및 smell 배열 생성
		map = new Tower[N][M];


		for(int i = 0; i < N; i++) {
			line = bf.readLine().split(" ");
			for(int j = 0; j < M; j++) {
				// 맵을 채우기
				map[i][j] = new Tower(i,j,Integer.parseInt(line[j]), 0);
			}
		}
		
		// k는 1부터 K까지 돌기
		for(int k = 1; k <= K; k++) {
			boolean isAttack = findTarget(k);
			attacked = new boolean[N][M];
			for(int i = 0; i < N; i++) {
				Arrays.fill(attacked[i], false);
			}
			
//			System.out.println("============================");
//			Print();
			
			if(isAttack) {
				// BFS로 최단 경로를 찾고, 없으면 -1
				int shortest = BFS_findN();
//				System.out.println(shortest);
	
				// 최단 경로가 -1이 아니면 레이져 공격 ㄱㄱ 
				if(shortest != -1) razerAttack(shortest);
				// 그 외에는 포탄 공격하기
				else potanAttack();
				
				// 이후 리빌딩하기
				reBuilding();

			}
			
			
			
			
//			Print();
		}

		findTarget(K+1);
		System.out.println(defence.power);
		
	}
	private static void reBuilding() {
		// 공격과 상관없는 것들은 정비하기
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				// 부서졌거나 공격과 연관이 있으면 넘어감
				 if(map[i][j].power <= 0 || attacked[i][j]) continue;
				 
				 // 그게 아니면 공격력 증가
				 map[i][j].power++;
			}
		}
	}
	
	private static void potanAttack() {
		attacked[attack.i][attack.j] = true;
		
		// 본진 타격
		map[defence.i][defence.j].power -= attack.power;
		if(map[defence.i][defence.j].power <= 0) {
			map[defence.i][defence.j].power = 0;
		}
		attacked[defence.i][defence.j] = true;
		
		// 주변 타격
		for(int k = 0; k < 8; k++) {
			int nr = defence.i + delta8[k][0];
			int nc = defence.j + delta8[k][1];
			
			// 0보다 작아지면 최하단으로, 커지면 최상단으로
			if(0 > nr) nr = N-1;
			else if(N-1 < nr) nr = 0;
			
			if(0 > nc) nc = M-1;
			else if(M-1 < nc) nc = 0;
			
			if(nr == attack.i && nc == attack.j) continue;
			
			map[nr][nc].power -= (attack.power/2);
			if(map[nr][nc].power <= 0) {
				map[nr][nc].power = 0;
			}
			attacked[nr][nc] = true;
		}
	}
	
	private static void razerAttack(int shortest) {
		// 최단 경로가 
		dfs_visited = new boolean[N][M];
		dfs_visited[attack.i][attack.j] = true;
		attacked[attack.i][attack.j] = true;
		
		DFS(shortest, new Node(attack.i, attack.j, 0));
	}
	
	private static boolean DFS(int Max, Node node) {
//		System.out.println(node.toString());
		if(node.i == defence.i && node.j == defence.j) {
			
			// 공격을 하고 넘어가자!!!
			map[node.i][node.j].power -= attack.power;
			if(map[node.i][node.j].power <= 0) {
				map[node.i][node.j].power = 0;
			}
			
			attacked[node.i][node.j] = true;
			return true; // true를 넘겨 주면 공격 ㄱㄱ다.
		}
		
		if(node.order == Max) return false;
		
		// 아직 스텝이 남았으면 간다
		for(int k = 0 ; k < 4; k++) {
			int nr = node.i + delta[k][0];
			int nc = node.j + delta[k][1];
			
			if(0 > nr) nr = N-1;
			else if(N-1 < nr) nr = 0;
			
			if(0 > nc) nc = M-1;
			else if(M-1 < nc) nc = 0;
			
			if(0 <= nr && nr < N && 0 <= nc && nc < M 
					&& !dfs_visited[nr][nc] && map[nr][nc].power != 0) {
				dfs_visited[nr][nc] = true;
				
				// true면 나도 공격 받아
				if(DFS(Max, new Node(nr, nc, node.order+1))) {
					// 공격자면 stop
					if(node.i == attack.i && node.j == attack.j) return true;
					
					map[node.i][node.j].power -= (attack.power/2);
					if(map[node.i][node.j].power <= 0) {
						map[node.i][node.j].power = 0;
					}
					attacked[node.i][node.j] = true;
					
					return true;
				}
				
				dfs_visited[nr][nc] = false;
			}
		}
		
		return false;
	}
	
	private static int BFS_findN() {
		boolean[][] visited = new boolean[N][M];
		
		queue.add(new Node(attack.i, attack.j, 0));
		visited[attack.i][attack.j] = true;
		
		while(!queue.isEmpty()) {
			Node node = queue.poll();
			if(node.i == defence.i && node.j == defence.j) {
				while(!queue.isEmpty()) queue.poll();
				return node.order;
			}
			
			for(int k = 0 ; k < 4; k++) {
				int nr = node.i + delta[k][0];
				int nc = node.j + delta[k][1];
				
				if(0 > nr) nr = N-1;
				else if(N-1 < nr) nr = 0;
				
				if(0 > nc) nc = M-1;
				else if(M-1 < nc) nc = 0;
				
				if(0 <= nr && nr < N && 0 <= nc && nc < M && !visited[nr][nc]
						&& map[nr][nc].power != 0 ) {
					visited[nr][nc] = true;
					queue.add(new Node(nr, nc, node.order + 1));
				}
			}
		}
		
		return -1;
	}
	
	private static boolean findTarget(int now_k) {
		// 공격자는 최소 값을 갱신하고, 방어자는 최댓값으로 갱신하기
		attack = new Tower(0,0,5001,0);
		defence = new Tower(0,0,-1,0);
		
		// 공격자 방어자 찾기
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				if(map[i][j].power == 0) continue;
				
				// 0이 아닌 포탑은 선정과정을 거침.
				if(map[i][j].compareTo(attack) == -1) {
					attack = map[i][j];
				}
				if(map[i][j].compareTo(defence) == 1) {
					defence = map[i][j];
				}
			}
		}
		
		// 아무도 공격하지 않는다
		if(defence.i == attack.i && defence.j == attack.j) {
			return false;
		}
		
//		System.out.println("디펜스 : "+defence.toString());
		
		// 공격자에 힘을 주기
		map[attack.i][attack.j].power += (N+M);
		map[attack.i][attack.j].recent_k = now_k;
		attack = map[attack.i][attack.j];
		return true;
	}
	
	private static void Print() {
		// 맵출력
		System.out.println("맵출력");
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < M; j++) {
				System.out.print(map[i][j].toString()+" ");
			}
			System.out.println();
		}
		
		// 공격자 방어자
		System.out.println("공격자 방어자");
		System.out.println(attack.toString() + " -> " + defence.toString());
	}
}

class Node{
	int i;
	int j;
	int order;
	
	Node(int i, int j, int order){
		this.i = i;
		this.j = j;
		this.order = order;
	}

	@Override
	public String toString() {
		return "Node [i=" + i + ", j=" + j + ", order=" + order + "]";
	}
	
	
}

class Tower<T> implements Comparable<T>{
	int i;
	int j;
	int power;
	int recent_k;
	
	Tower(int i, int j, int power, int recent_k){
		this.i = i;
		this.j = j;
		this.power = power;
		this.recent_k = recent_k;
	}

	@Override
	public String toString() {
		return "[" + power + "(" + recent_k + ")]";
	}

	@Override
	public int compareTo(T o) {
		Tower tower = (Tower) o;
		// 파워가 크고, 공격한지가 낮고, 행렬합이 작고ㅡ 열값이 작으면 큰것
		if(this.power > tower.power) return 1;
		if(this.power < tower.power) return -1;
		
		if(this.recent_k < tower.recent_k) return 1;
		if(this.recent_k > tower.recent_k) return -1;
		
		if(this.i + this.j < tower.i + tower.j) return 1;
		if(this.i + this.j > tower.i + tower.j) return -1;
		
		if(this.j < tower.j) return 1;
		if(this.j > tower.j) return -1;
		
		return 0;
	}
	
	public void reset() {
		this.i = 0;
		this.j = 0;
		this.power = 0;
		this.recent_k = 0;
	}
}