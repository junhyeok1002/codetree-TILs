import java.util.*;
import java.io.*;

public class Main {
	static int N, M, K;
	static int[][] map, pMap;
	static Person[] persons;
	static boolean[] success;
	static int[] end;
	
	
	public static void main(String[] args) throws Exception {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		String[] line;
		
		// 첫번째, 두번쨰 줄 입력

		line = bf.readLine().split(" ");
		N = Integer.parseInt(line[0]);
		M = Integer.parseInt(line[1]);
		K = Integer.parseInt(line[2]);
		
		map = new int[N][N];
		pMap = new int[N][N];
		persons = new Person[M+1];
		success = new boolean[M+1];

		for(int i = 0 ; i < N ; i++) {
			line = bf.readLine().split(" ");
			for(int j = 0 ; j < N ; j++) {
				map[i][j] = Integer.parseInt(line[j]);
			}
		}
		
		// 사람 정보 등록
		for(int i = 1 ; i <= M; i++) {
			line = bf.readLine().split(" ");
			int r = Integer.parseInt(line[0])-1;
			int c = Integer.parseInt(line[1])-1;
			
			persons[i] = new Person(i, r, c);
			pMap[r][c] = i;
		}
		
		// 목적지
		line = bf.readLine().split(" ");
		int r = Integer.parseInt(line[0])-1;
		int c = Integer.parseInt(line[1])-1;
		map[r][c] = -1;
		end = new int[]{r,c};
		
		for(int k = 1; k <= K; k++) {
			personMove(); 
			Rotate(); 
		}
		
		int sum = 0;
		for(int i = 1; i <= M; i++) {
			sum += persons[i].moveCnt;
		}
		
		System.out.println(sum);
		System.out.println((end[0]+1) +" "+(end[1]+1));
	
	}
	
	private static int[] getStartPoint(int r1, int c1, int r2, int c2) {
		int[] ans = new int[2];
		
		ans[0] = Math.min(r1, r2);
		ans[1] = Math.min(c1, c2);
		
		// 세로가 더 길면 
		if(Math.abs(r1-r2) > Math.abs(c1-c2)) {
			int temp_i = Math.max(r1, r2) - getSquareN(r1,c1,r2,c2);
			int temp_j = Math.max(c1, c2) - getSquareN(r1,c1,r2,c2);
			
			while(!(0 <= temp_i && temp_i < N && 0 <= temp_j && temp_j < N)) {
				temp_j++;
			}
			
			ans[0] = temp_i;
			ans[1] = temp_j;
		}
		
		// 가로가 더길면
		else if(Math.abs(r1-r2) < Math.abs(c1-c2)) {
			int temp_i = Math.max(r1, r2) - getSquareN(r1,c1,r2,c2);
			int temp_j = Math.max(c1, c2) - getSquareN(r1,c1,r2,c2);
			
			while(!(0 <= temp_i && temp_i < N && 0 <= temp_j && temp_j < N)) {
				temp_i++;
			}
			
			ans[0] = temp_i;
			ans[1] = temp_j;
		}
		
		return ans;
	}
	
	private static void Rotate() {
		Person standard = selectRotateStandard();
		
		int distance = getSquareN(standard.i, standard.j, end[0], end[1]);
		
		int[] starts = getStartPoint(standard.i, standard.j, end[0], end[1]);
		int start_i = starts[0], start_j = starts[1];
		
		// start_i, start_j 부터 => distance만큼 잡아
		int[][] temp = new int[distance+1][distance+1];
		
		for(int i = start_i; i < start_i+distance+1; i++) {
			for(int j = start_j; j < start_j+distance+1; j++) {
				
				temp[j-start_j][distance-(i-start_i)] = map[i][j];
				if(temp[j-start_j][distance-(i-start_i)] > 0) {
					temp[j-start_j][distance-(i-start_i)]--;
				}
				
				// 목적지면 목적지 변경
				if(map[i][j] == -1) {
					end[0] = start_i + j-start_j;
					end[1] = start_j + distance-(i-start_i);
				}
				
				// 맵에 사람이 있으면!!~! 바꿔줘
				if(pMap[i][j] != 0) {
					int id = pMap[i][j];
					
					persons[id].i = start_i + j-start_j;
					persons[id].j = start_j + distance-(i-start_i);
					pMap[i][j] = 0;
				}
			}
		}
		
		for(int i = start_i; i < start_i+distance+1; i++) {
			for(int j = start_j; j < start_j+distance+1; j++) {
				map[i][j] = temp[i-start_i][j-start_j];
			}
		}
		
		// 사람 채워주기
		for(int i = 1; i <= M; i++) {
			if(success[i]) continue;

			pMap[persons[i].i][persons[i].j] = persons[i].id; 
		}
	}
	
	// 최대 거리만큼 간 것이!!!!! 범위 안에 있어야 한다!!!!!!!!!!!!
	private static Person selectRotateStandard() {
		Person standard = null;
		int min_distance = Integer.MAX_VALUE;
		
		for(int i = 1; i <= M; i++) {
			if(success[i]) continue; // 나간 사람이면 넘어감
			
			// 거리가 맥스가 아니면!! 갈 수는 있다는 뜻이므로... 맥스인 것 다 거르기
			if(selectRotateArea(persons[i].i, persons[i].j, end[0], end[1]) == 5) {
//				System.out.println(persons[i].toString() + " 은 걸러졌따.");
				continue;
			}
			
			// 초기화
			if(standard == null) {
				standard = persons[i];
				min_distance = getSquareN(standard.i, standard.j, end[0], end[1]);
			}
		
			Person p = persons[i];
			if(min_distance > getSquareN(p.i, p.j, end[0], end[1])) {
				min_distance = getSquareN(p.i, p.j, end[0], end[1]);
				standard = p;
			}
			else if(min_distance == getSquareN(p.i, p.j, end[0], end[1])) {
				// 현재의 로테이션 에어리어가 최소기준점보다 작으면 갱신
				if(selectRotateArea(p.i, p.j, end[0], end[1]) < selectRotateArea(standard.i, standard.j, end[0], end[1])) {
					min_distance = getSquareN(p.i, p.j, end[0], end[1]);
					standard = p;
				}
			}	
		}
//		System.out.println("기준점 : " + standard.toString());

		return standard;
	}
	
	private static int getSquareN(int r1, int c1, int r2, int c2) {
		return Math.max(Math.abs(r1-r2), Math.abs(c1-c2));
	}
	
	private static int selectRotateArea(int r1, int c1, int r2, int c2) {
		int nr = 0, nc = 0, area = 5;
		int distance = Math.max(Math.abs(r1-r2), Math.abs(c1-c2));
		
		nr = r2 - distance;
		nc = c2 - distance;
		if(r1 <= r2 && c1 <= c2 && 0 <= nr && nr < N && 0 <= nc && nc < N) {
			area = 1;
			return area;
		}
		
		nr = r2 - distance;
		nc = c2 + distance;
		if(r1 <= r2 && c1 >= c2 && 0 <= nr && nr < N && 0 <= nc && nc < N) {
			area = 2;
			return area;
		}
		
		nr = r2 + distance;
		nc = c2 - distance;
		if(r1 >= r2 && c1 <= c2 && 0 <= nr && nr < N && 0 <= nc && nc < N) {
			area = 3;
			return area;
		}
		
		nr = r2 + distance;
		nc = c2 + distance;
		if(r1 >= r2 && c1 >= c2 && 0 <= nr && nr < N && 0 <= nc && nc < N) {
			area = 4;
			return area;
		}
		
		return area;
	}
	
	private static void personMove() {
		for(int i = 1; i <= M; i++) {
			if(success[i]) continue; // 나간 사람이면 넘어감
				
			Person p = persons[i];
			
			// 사람이 목표한 곳을 바라본다 어디로 가야하오...
			String direct = findDirect(p.i, p.j, end[0], end[1]);
			boolean isMove = false;
			
			// 수직방향 이동해보자
			if(direct.charAt(0) == 'd') {
				// 아래로 이동 했을때 범위 안이고 벽이 없으면
				if(p.i+1 < N && map[p.i+1][p.j] <= 0) {
					isMove = true;
					pMap[p.i][p.j] = 0;
					pMap[p.i+1][p.j] = p.id;
					 p.i = p.i+1;
					 p.moveCnt++;
				}
			}
			else if(direct.charAt(0) == 'u') {
				// 아래로 이동 했을때 범위 안이고 벽이 없으면
				if(0 <= p.i-1 && map[p.i-1][p.j] <= 0) {
					isMove = true;
					pMap[p.i][p.j] = 0;
					pMap[p.i-1][p.j] = p.id;
					 p.i = p.i-1;
					 p.moveCnt++;
				}
			}
			
			// 수직이동 안했을때 수평방향 이동해보자
			if(!isMove && direct.charAt(1) == 'r') {
				// 아래로 이동 했을때 범위 안이고 벽이 없으면
				if(p.j+1 < N && map[p.i][p.j+1] <= 0) {
					isMove = true;
					 pMap[p.i][p.j] = 0;
					 pMap[p.i][p.j+1] = p.id;
					 p.j = p.j+1;
					 p.moveCnt++;
				}
			}
			else if(!isMove && direct.charAt(1) == 'l') {
				// 아래로 이동 했을때 범위 안이고 벽이 없으면
				if(0 <= p.j-1 && map[p.i][p.j-1] <= 0) {
					isMove = true;
					pMap[p.i][p.j] = 0;
					pMap[p.i][p.j-1] = p.id;
					 p.j = p.j-1;
					 p.moveCnt++;
				}
			}
			
			if(map[p.i][p.j] == -1) {
				success[i] = true;
				pMap[p.i][p.j] = 0;
			}
			
//			System.out.println(direct);
			
		}
	}
	
	// start -> end
	private static String findDirect(int r1, int c1, int r2, int c2) {
		StringBuilder ans = new StringBuilder();
		if(r1 < r2) ans.append("d");
		else if(r1 > r2) ans.append("u");
		else ans.append("s");
		
		if(c1 < c2) ans.append("r");
		else if(c1 > c2) ans.append("l");
		else ans.append("s");
		
		return ans.toString();
	}
	
	private	static void Print() {
		System.out.println("진짜 맵");
		for(int i = 0 ; i < N ; i++) {
			for(int j = 0 ; j < N ; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("사람 맵");
		for(int i = 0 ; i < N ; i++) {
			for(int j = 0 ; j < N ; j++) {
				System.out.print(pMap[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("사람");
		System.out.println(Arrays.toString(persons));
		
		System.out.println();
		System.out.println("끝점 :" + Arrays.toString(end));
	}
}
class Person{
	int id;
	int i;
	int j;
	int moveCnt;
	
	public Person(int id, int i, int j) {
		this.id = id;
		this.i = i;
		this.j = j;
		this.moveCnt = 0;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", i=" + i + ", j=" + j + ", moveCnt=" + moveCnt + "]";
	}
}