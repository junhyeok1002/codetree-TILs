import java.util.*;
import java.io.*;

public class Main {
	static int Q, N, M, P;
	static HashMap<Integer, Rabbit> hashmap = new HashMap<>();
	static int standard_id;
	static Set<Integer> set = new HashSet<>();
	static long total_sum_score = 0;
	
	public static void main(String[] args) throws Exception {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		String[] line;
		
		// 첫번째, 두번쨰 줄 입력
		Q = Integer.parseInt(bf.readLine());
		
		line = bf.readLine().split(" ");
		N = Integer.parseInt(line[1]);
		M = Integer.parseInt(line[2]);
		P = Integer.parseInt(line[3]);
		standard_id  = Integer.parseInt(line[4]);
		
		for(int i = 4 ; i < line.length; i+=2) {
			int id = Integer.parseInt(line[i]);
			int d = Integer.parseInt(line[i+1]);
			
			hashmap.put(id, new Rabbit(id, d));
		}
		
		for(int q = 1; q < Q; q++) {
			line = bf.readLine().split(" ");
			
			int command = Integer.parseInt(line[0]);
			
			
			if(command == 400) {
				Rabbit max = getMaxRabbit();
				System.out.println(max.score+total_sum_score);
			}
			
			else if(command == 300) {
				int id = Integer.parseInt(line[1]);
				int L = Integer.parseInt(line[2]);
				
				hashmap.get(id).d *= L;
			}
			
			else if(command == 200) {
				int K = Integer.parseInt(line[1]);
				int S = Integer.parseInt(line[2]);
				
				for(int k = 0; k < K; k++) {
					Rabbit rabbit = getJumpRabbit();
					
					set.add(rabbit.id);
					rabbit.jumpCnt++;
					
					int[] cordi = getCordi(rabbit);
					rabbit.i = cordi[0];
					rabbit.j = cordi[1];
					
					rabbit.score -= (long)(rabbit.i + rabbit.j);
					total_sum_score += (long)(rabbit.i + rabbit.j);
					
				}
				getScoreRabbit().score += (long)S;
				
			}
			
		}

	}
	
	private static int[] getCordi(Rabbit rabbit) {
		int[] cordi = {-1,-1}; 
		int left, rabbit_i, rabbit_j;
		
		// 상 이동
		left = rabbit.d % (((N-rabbit.i) + (rabbit.i-1) ) * 2);
		rabbit_i = rabbit.i;
		rabbit_j = rabbit.j;
		while(true) {
			if(rabbit_i - left >= 1) {
				cordi = biggerCordi(cordi, new int[]{rabbit_i - left, rabbit_j});
				break;
			}
			else {
				left -= (rabbit_i-1);
				rabbit_i = 1;
				
				if(rabbit_i + left <= N) {
					cordi = biggerCordi(cordi, new int[]{rabbit_i + left, rabbit_j});
					break;
				}
				else {
					left -= (N-rabbit_i);
					rabbit_i = N;
				}
			}
		}
		
		// 하 이동
		left = rabbit.d % (((N-rabbit.i) + (rabbit.i-1) ) * 2);
		rabbit_i = rabbit.i;
		rabbit_j = rabbit.j;
		while(true) {
			if(rabbit_i + left <= N) {
				cordi = biggerCordi(cordi, new int[]{rabbit_i + left, rabbit_j});
				break;
			}
			else {
				left -= (N-rabbit_i);
				rabbit_i = N;
				
				if(rabbit_i - left >= 1) {
					cordi = biggerCordi(cordi, new int[]{rabbit_i - left, rabbit_j});
					break;
				}
				else {
					left -= (rabbit_i-1);
					rabbit_i = 1;
				}
			}
		}
		
		// 좌 이동
		left = rabbit.d % (((M-rabbit.j) + (rabbit.j-1) ) * 2);
		rabbit_i = rabbit.i;
		rabbit_j = rabbit.j;
		while(true) {
			if(rabbit_j - left >= 1) {
				cordi = biggerCordi(cordi, new int[]{rabbit_i , rabbit_j - left});
				break;
			}
			else {
				left -= (rabbit_j-1);
				rabbit_j = 1;
				
				if(rabbit_j + left <= M) {
					cordi = biggerCordi(cordi, new int[]{rabbit_i, rabbit_j + left});
					break;
				}
				else {
					left -= (M-rabbit_j);
					rabbit_j = M;
				}
			}
		}
		
		// 우 이동
		left = rabbit.d % (((M-rabbit.j) + (rabbit.j-1) ) * 2);
		rabbit_i = rabbit.i;
		rabbit_j = rabbit.j;
		while(true) {
			if(rabbit_j + left <= M) {
				cordi = biggerCordi(cordi, new int[]{rabbit_i, rabbit_j + left});
				break;
			}
			else {
				left -= (M-rabbit_j);
				rabbit_j = M;
				
				if(rabbit_j - left >= 1) {
					cordi = biggerCordi(cordi, new int[]{rabbit_i, rabbit_j - left});
					break;
				}
				else {
					left -= (rabbit_j-1);
					rabbit_j = 1;
				}
			}
		}

		
		return cordi;
	}

	
	private static int[] biggerCordi(int[] cordi1, int[] cordi2) {
		if(cordi1[0]+cordi1[1] > cordi2[0]+cordi2[1]) return cordi1;
		if(cordi1[0]+cordi1[1] < cordi2[0]+cordi2[1]) return cordi2;
		
		if(cordi1[0] > cordi2[0]) return cordi1;
		if(cordi1[0] < cordi2[0]) return cordi2;
		
		if(cordi1[1] > cordi2[1]) return cordi1;
		if(cordi1[1] < cordi2[1]) return cordi2;
		
		return cordi1;
	}
	
	private static Rabbit getScoreRabbit() {
		Rabbit max = new Rabbit(0, 0);
		
		boolean first = true;
		for(int id : set) {
			Rabbit now = hashmap.get(id);
			
			if(first) {
				first = false;
				max = now;
				continue;
			}
			
			if(now.i+now.j > max.i+max.j) {
				max = now;
				continue;
			}
			if(now.i+now.j < max.i+max.j) continue;
			
			if(now.i > max.i) {
				max = now;
				continue;
			}
			if(now.i < max.i) continue;
			
			if(now.j > max.j) {
				max = now;
				continue;
			}
			if(now.j < max.j) continue;
		}
		
		set.clear();
		return max;
	}
	
	private static Rabbit getJumpRabbit(){
		Rabbit max = hashmap.get(standard_id);
		for(int id : hashmap.keySet()) {
			Rabbit now = hashmap.get(id);
			
			if(now.jumpCnt < max.jumpCnt) {
				max = now;
				continue;
			}
			if(now.jumpCnt > max.jumpCnt) continue;
			
			if(now.i+now.j < max.i+max.j) {
				max = now;
				continue;
			}
			if(now.i+now.j > max.i+max.j) continue;
			
			if(now.i < max.i) {
				max = now;
				continue;
			}
			if(now.i > max.i) continue;
			
			if(now.j < max.j) {
				max = now;
				continue;
			}
			if(now.j > max.j) continue;
			
			if(now.id < max.id) {
				max = now;
				continue;
			}
			if(now.id > max.id) continue;
		}

		return max;
	}
	
	
	
	private static Rabbit getMaxRabbit() {
		Rabbit max = hashmap.get(standard_id);
		for(int id : hashmap.keySet()) {
			if(max.score < hashmap.get(id).score) {
				max = hashmap.get(id);
			}
		}
		return max;
	}

}
class Rabbit<T> implements Comparable<T>{
	int id;
	int d;
	int i;
	int j;
	int jumpCnt;
	long score;
	
	Rabbit(int id, int d){
		this.id = id;
		this.d = d;
		this.i = 1;
		this.j = 1;
		this.jumpCnt = 0;
		this.score = 0;
	}
	
	@Override
	public int compareTo(T o) {
		Rabbit comp = (Rabbit) o;
		
		if(this.jumpCnt < comp.jumpCnt) return 1;
		if(this.jumpCnt > comp.jumpCnt) return -1;
		
		if(this.i+this.j < comp.i+comp.j) return 1;
		if(this.i+this.j > comp.i+comp.j) return -1;
		
		if(this.i < comp.i) return 1;
		if(this.i > comp.i) return -1;
		
		if(this.j < comp.j) return 1;
		if(this.j > comp.j) return -1;
		
		if(this.id < comp.id) return 1;
		if(this.id > comp.id) return -1;
		
		return 0;
	}

	@Override
	public String toString() {
		return "Rabbit [id=" + id + ", d=" + d + ", i=" + i + ", j=" + j + ", jumpCnt=" + jumpCnt + ", score=" + score
				+ "]";
	}
}