<template>
  <el-card class="page-card">
    <div class="page-header">
      <div>
        <h2>{{ problem.title }}</h2>
        <p class="subtitle">巩固算法与数据结构的在线编程练习</p>
      </div>
      <div class="meta-tags">
        <el-tag type="warning">时间限制：{{ problem.timeLimit }}</el-tag>
        <el-tag type="info">空间限制：{{ problem.memoryLimit }}</el-tag>
      </div>
    </div>

    <el-row :gutter="20" class="problem-layout">
      <el-col :xs="24" :lg="12">
        <section class="section-card">
          <header class="section-header">
            <h3>题目描述</h3>
            <el-tag type="success">支持语言：{{ problem.languages.join(' / ') }}</el-tag>
          </header>
          <p class="paragraph">{{ problem.description }}</p>

          <h4 class="section-title">输入格式</h4>
          <p class="paragraph">{{ problem.inputFormat }}</p>

          <h4 class="section-title">输出格式</h4>
          <p class="paragraph">{{ problem.outputFormat }}</p>

          <el-divider />
          <h4 class="section-title">测试样例</h4>
          <el-collapse accordion>
            <el-collapse-item
              v-for="(sample, index) in problem.samples"
              :key="index"
              :name="index"
              :title="`样例 ${index + 1}`"
            >
              <div class="sample-block">
                <p class="sample-label">输入</p>
                <pre class="sample-code">{{ sample.input }}</pre>
              </div>
              <div class="sample-block">
                <p class="sample-label">输出</p>
                <pre class="sample-code">{{ sample.output }}</pre>
              </div>
              <p class="sample-explanation">{{ sample.explanation }}</p>
            </el-collapse-item>
          </el-collapse>
        </section>
      </el-col>

      <el-col :xs="24" :lg="12">
        <section class="section-card editor-card">
          <header class="section-header">
            <h3>代码编辑</h3>
            <div class="editor-actions">
              <el-select v-model="selectedLanguage" class="language-select" size="small" @change="resetTemplate">
                <el-option
                  v-for="language in problem.languages"
                  :key="language"
                  :label="language"
                  :value="language"
                />
              </el-select>
              <el-button size="small" type="primary" plain @click="resetTemplate">重置模板</el-button>
            </div>
          </header>

          <el-input
            v-model="code"
            type="textarea"
            :rows="20"
            resize="none"
            spellcheck="false"
            class="code-editor"
            placeholder="在此编写你的解题代码"
          />

          <div class="submit-actions">
            <el-button type="primary">提交代码</el-button>
            <el-button>运行样例</el-button>
          </div>
        </section>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'

const problem = {
  title: '两数之和',
  description: '给定一个整数数组和一个目标值，请返回数组中和为目标值的两个数的下标。保证每个输入只对应唯一答案，但数组中同一个元素不能重复使用。',
  inputFormat: '第一行输入整数 n，表示数组长度；第二行输入 n 个整数；第三行输入目标值 target。',
  outputFormat: '输出两个下标，按从小到大顺序，用空格分隔。',
  timeLimit: '1s',
  memoryLimit: '256MB',
  languages: ['C', 'C++', 'Java', 'Python'],
  samples: [
    {
      input: '4\n2 7 11 15\n9',
      output: '0 1',
      explanation: '2 + 7 = 9，返回它们在数组中的下标 0 和 1。'
    },
    {
      input: '5\n1 3 4 2 6\n10',
      output: '2 4',
      explanation: '4 + 6 = 10，对应下标 2 和 4。'
    }
  ]
}

const templates = {
  C: `#include <stdio.h>\n\nint main() {\n    int n, target;\n    if (scanf("%d", &n) != 1) return 0;\n    int nums[n];\n    for (int i = 0; i < n; i++) scanf("%d", &nums[i]);\n    scanf("%d", &target);\n    for (int i = 0; i < n; i++) {\n        for (int j = i + 1; j < n; j++) {\n            if (nums[i] + nums[j] == target) {\n                printf("%d %d", i, j);\n                return 0;\n            }\n        }\n    }\n    return 0;\n}`,
  'C++': `#include <bits/stdc++.h>\nusing namespace std;\n\nint main() {\n    ios::sync_with_stdio(false);\n    cin.tie(nullptr);\n\n    int n, target;\n    if (!(cin >> n)) return 0;\n    vector<int> nums(n);\n    for (int i = 0; i < n; i++) cin >> nums[i];\n    cin >> target;\n\n    unordered_map<int, int> mp;\n    for (int i = 0; i < n; i++) {\n        int need = target - nums[i];\n        if (mp.count(need)) {\n            cout << mp[need] << ' ' << i;\n            return 0;\n        }\n        mp[nums[i]] = i;\n    }\n    return 0;\n}`,
  Java: `import java.util.*;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        if (!sc.hasNextInt()) return;\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n\n        Map<Integer, Integer> map = new HashMap<>();\n        for (int i = 0; i < n; i++) {\n            int need = target - nums[i];\n            if (map.containsKey(need)) {\n                System.out.println(map.get(need) + " " + i);\n                return;\n            }\n            map.put(nums[i], i);\n        }\n    }\n}`,
  Python: `n = int(input())\nnums = list(map(int, input().split()))\ntarget = int(input())\n\nindex_map = {}\nfor i, num in enumerate(nums):\n    need = target - num\n    if need in index_map:\n        print(index_map[need], i)\n        break\n    index_map[num] = i\n`
}

const selectedLanguage = ref(problem.languages[0])
const code = ref(templates[selectedLanguage.value])

const resetTemplate = () => {
  code.value = templates[selectedLanguage.value]
}
</script>

<style scoped>
.page-card {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.subtitle {
  margin: 4px 0 0;
  color: #606266;
}

.meta-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.problem-layout {
  margin-top: 16px;
}

.section-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  height: 100%;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.section-title {
  margin: 12px 0 4px;
  font-weight: 600;
}

.paragraph {
  color: #4b5563;
  line-height: 1.6;
  white-space: pre-wrap;
}

.sample-block {
  margin: 8px 0;
}

.sample-label {
  font-weight: 600;
  margin-bottom: 4px;
}

.sample-code {
  background: #1f2937;
  color: #e5e7eb;
  padding: 10px;
  border-radius: 6px;
  overflow-x: auto;
}

.sample-explanation {
  color: #6b7280;
  margin-top: 4px;
}

.editor-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.editor-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.language-select {
  width: 140px;
}

.code-editor :deep(.el-textarea__inner) {
  font-family: 'Fira Code', 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 14px;
  line-height: 1.5;
  min-height: 360px;
}

.submit-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
