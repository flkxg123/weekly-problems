package class_2023_01_1_week;

// 线段树建边
// 测试链接 : https://www.luogu.com.cn/problem/P1983
// 请同学们务必参考如下代码中关于输入、输出的处理
// 这是输入输出处理效率很高的写法
// 提交如下方法，把主类名改成Main，可以直接通过
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.util.Arrays;

public class Code05_BusStationsMinLevelNumbers {

	public static final int maxn = 100001;
	// 停靠车站
	public static int[] stops = new int[maxn];
	// 一段线段树范围的id编号
	public static int[] id = new int[maxn << 2];
	// id点是否为单点
	public static boolean[] single = new boolean[maxn << 3];
	// id点的入度
	public static int[] inDegree = new int[maxn << 3];
	// id点拓扑排序统计的最大深度
	public static int[] deep = new int[maxn << 3];
	// 链式前向星建图用
	public static int[] head = new int[maxn << 3];
	public static int[] to = new int[maxn << 3];
	public static int[] next = new int[maxn << 3];
	// 拓扑排序用
	public static int[] queue = new int[maxn << 3];
	// n为车站个数、nth为线段树上范围的编号计数、eth为边的计数
	public static int n, nth, eth;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StreamTokenizer in = new StreamTokenizer(br);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
		while (in.nextToken() != StreamTokenizer.TT_EOF) {
			n = (int) in.nval;
			in.nextToken();
			int m = (int) in.nval;
			nth = 0;
			eth = 0;
			Arrays.fill(single, 0, (n << 2) + m + 1, false);
			Arrays.fill(inDegree, 0, (n << 2) + m + 1, 0);
			Arrays.fill(deep, 0, (n << 2) + m + 1, 0);
			build(1, n, 1);
			for (int i = 0; i < m; i++) {
				in.nextToken();
				int k = (int) in.nval;
				for (int j = 0; j < k; j++) {
					in.nextToken();
					stops[j] = (int) in.nval;
				}
				int curVirtual = ++nth;
				// 虚点向停靠车站连边
				for (int j = 0; j < k; j++) {
					vLinkSingle(curVirtual, stops[j], 1, n, 1);
				}
				// 不停靠的连续车站向虚点连边
				for (int j = 1; j < k; j++) {
					if (stops[j] > stops[j - 1] + 1) {
						rangeLinkV(stops[j - 1] + 1, stops[j] - 1, curVirtual, 1, n, 1);
					}
				}
			}
			out.println(topoSort());
			out.flush();
		}

	}

	public static void build(int l, int r, int rt) {
		id[rt] = ++nth;
		if (l == r) {
			single[id[rt]] = true;
		} else {
			int m = (l + r) / 2;
			build(l, m, rt << 1);
			build(m + 1, r, rt << 1 | 1);
			addEdge(id[rt << 1], id[rt]);
			addEdge(id[rt << 1 | 1], id[rt]);
		}
	}

	public static void rangeLinkV(int L, int R, int vid, int l, int r, int rt) {
		if (L <= l && r <= R) {
			addEdge(id[rt], vid);
		} else {
			int m = (l + r) / 2;
			if (L <= m) {
				rangeLinkV(L, R, vid, l, m, rt << 1);
			}
			if (R > m) {
				rangeLinkV(L, R, vid, m + 1, r, rt << 1 | 1);
			}
		}
	}

	public static void vLinkSingle(int vid, int single, int l, int r, int rt) {
		if (l == r) {
			addEdge(vid, id[rt]);
		} else {
			int m = (l + r) / 2;
			if (single <= m) {
				vLinkSingle(vid, single, l, m, rt << 1);
			} else {
				vLinkSingle(vid, single, m + 1, r, rt << 1 | 1);
			}
		}
	}

	public static void addEdge(int fid, int tid) {
		inDegree[tid]++;
		to[++eth] = tid;
		next[eth] = head[fid];
		head[fid] = eth;
	}

	public static int topoSort() {
		int l = 0;
		int r = 0;
		for (int i = 1; i <= nth; i++) {
			if (inDegree[i] == 0) {
				queue[r++] = i;
				if (single[i]) {
					deep[i] = 1;
				}
			}
		}
		int ans = 0;
		while (l < r) {
			int curNode = queue[l++];
			ans = Math.max(ans, deep[curNode]);
			for (int edgeIndex = head[curNode]; edgeIndex != 0; edgeIndex = next[edgeIndex]) {
				int child = to[edgeIndex];
				deep[child] = Math.max(deep[child], deep[curNode] + (single[child] ? 1 : 0));
				if (--inDegree[child] == 0) {
					queue[r++] = child;
				}
			}
		}
		return ans;
	}

}