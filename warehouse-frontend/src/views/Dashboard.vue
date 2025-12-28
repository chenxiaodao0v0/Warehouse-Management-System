<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h2>仓库管理系统 - 数据总览</h2>
      <div class="dashboard-date">{{ currentDate }}</div>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="dashboard-cards">
      <el-col :span="6">
        <el-card class="card-item" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-blue">
              <i class="el-icon-goods"></i>
            </div>
            <div class="card-info">
              <div class="card-title">商品总数</div>
              <div class="card-value">{{ stats.goodsCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="card-item" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-green">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="card-info">
              <div class="card-title">仓库总数</div>
              <div class="card-value">{{ stats.warehouseCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="card-item" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-orange">
              <i class="el-icon-tickets"></i>
            </div>
            <div class="card-info">
              <div class="card-title">出入库记录</div>
              <div class="card-value">{{ stats.inOutRecordCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="card-item" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-purple">
              <i class="el-icon-s-home"></i>
            </div>
            <div class="card-info">
              <div class="card-title">企业信息</div>
              <div class="card-value">{{ stats.enterpriseName || stats.enterpriseCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="dashboard-charts">
      <el-col :span="14">
        <el-card class="chart-card" shadow="never">
          <div slot="header" class="chart-header">
            <span>近期出入库趋势</span>
          </div>
          <div id="trendChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="chart-card" shadow="never">
          <div slot="header" class="chart-header">
            <span>仓库库存分布</span>
          </div>
          <div id="warehouseChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 排行榜区域 -->
    <el-row :gutter="20" class="dashboard-ranking">
      <el-col :span="24">
        <el-card class="ranking-card" shadow="never">
          <div slot="header" class="chart-header">
            <span>进出货物数量前十排行</span>
            <div class="date-filter">
              <el-date-picker
                v-model="rankDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
                @change="onRankDateChange">
              </el-date-picker>
            </div>
          </div>
          <el-table :data="topTenData" style="width: 100%" :max-height="400">
            <el-table-column type="index" label="排名" width="80"></el-table-column>
            <el-table-column prop="enterpriseName" label="企业" width="200"></el-table-column>
            <el-table-column prop="goodsName" label="商品" width="200"></el-table-column>
            <el-table-column prop="inQuantity" label="入库数量" width="120"></el-table-column>
            <el-table-column prop="outQuantity" label="出库数量" width="120"></el-table-column>
            <el-table-column prop="totalQuantity" label="总数量" width="120"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新记录 -->
    <el-row :gutter="20" class="dashboard-recent">
      <el-col :span="24">
        <el-card class="recent-card" shadow="never">
          <div slot="header" class="chart-header">
            <span>最新出入库记录</span>
            <el-button type="text" @click="goToInOutRecord">查看更多</el-button>
          </div>
          <el-table :data="recentRecords" style="width: 100%" :max-height="300">
            <el-table-column prop="id" label="ID" width="150"></el-table-column>
            <el-table-column prop="goodsId" label="商品ID" width="100"></el-table-column>
            <el-table-column prop="warehouseId" label="仓库ID" width="100"></el-table-column>
            <el-table-column prop="type" label="类型" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.type === 1 ? 'success' : scope.row.type === 2 ? 'danger' : 'warning'">
                  {{ scope.row.type === 1 ? '入库' : scope.row.type === 2 ? '出库' : '调货' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
            <el-table-column prop="contactPerson" label="对接人" width="100"></el-table-column>
            <el-table-column prop="operateTime" label="操作时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.operateTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getStatistics, getTrendData, getWarehouseDistribution, getTopTenInOutData } from '@/api/statistics'
import { getInOutRecordList } from '@/api/inOutRecord'

export default {
  name: 'Dashboard',
  data() {
    return {
      currentDate: '',
      stats: {
        goodsCount: 0,
        warehouseCount: 0,
        inOutRecordCount: 0,
        enterpriseCount: 0,
        enterpriseName: '' // 添加企业名称字段
      },
      recentRecords: [],
      topTenData: [],
      rankDateRange: [],
      chartData: {
        trendData: {
          dates: [],
          inData: [],
          outData: [],
          transferData: []
        },
        warehouseData: []
      }
    }
  },
  mounted() {
    console.log('Dashboard mounted')
    this.getCurrentDate()
    this.loadStats()
    this.loadRecentRecords()
    this.loadChartData()
    this.loadTopTenData()
    // 移除这里的 renderCharts 调用，改由 loadChartData 完成后触发
  },
  methods: {
    getCurrentDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      this.currentDate = `${year}-${month}-${day}`
    },
    async loadStats() {
      try {
        console.log('Loading stats...')
        const res = await getStatistics()
        console.log('Full stats response:', res)
        
        // 确保响应格式正确
        if (res && (res.code === 200 || res.code === 0 || (res.goodsCount !== undefined))) {
          // 检查数据结构，兼容不同的响应格式
          let statsData = res.data
          if (res.data && res.data.goodsCount !== undefined) {
            // 如果数据在res.data下
            statsData = res.data
          } else if (res.goodsCount !== undefined) {
            // 如果数据直接在res下（已被拦截器处理）
            statsData = res
          }
          
          console.log('Processing stats data:', statsData)
          
          this.stats = {
            goodsCount: statsData.goodsCount || 0,
            warehouseCount: statsData.warehouseCount || 0,
            inOutRecordCount: statsData.inOutRecordCount || 0,
            enterpriseCount: statsData.enterpriseCount || 0,
            enterpriseName: statsData.enterpriseName || '' // 添加企业名称
          }
          console.log('Updated stats in component:', this.stats)
        } else {
          console.error('获取统计数据失败:', res ? (res.msg || res.message) : '响应为空')
          this.$message.error(res ? (res.msg || res.message || '获取统计数据失败') : '获取统计数据失败')
        }
      } catch (error) {
        console.error('获取统计数据失败 (catch):', error)
        this.$message.error('获取统计数据失败')
      }
    },
    
    async loadRecentRecords() {
      try {
        console.log('Loading recent records...')
        const params = {
          pageNum: 1,
          pageSize: 5
        }
        const res = await getInOutRecordList(params)
        console.log('Recent records response:', res)
        // 检查响应是否成功，兼容不同格式
        if (res && (res.code === 200 || res.code === 0 || res.records)) {
          // 尝试从不同可能的位置获取数据
          this.recentRecords = res.data?.records || res.records || []
          console.log('Updated recent records:', this.recentRecords)
        } else {
          console.error('获取最新记录失败:', res?.msg || res?.message || '响应格式不正确')
          this.$message.error(res?.msg || res?.message || '获取最新记录失败')
        }
      } catch (error) {
        console.error('获取最新记录失败 (catch):', error)
        this.$message.error('获取最新记录失败')
      }
    },
    
    async loadChartData() {
      try {
        console.log('Loading chart data...')
        // 获取趋势数据
        const trendParams = {
          days: 7
        }
        const trendRes = await getTrendData(trendParams)
        console.log('Trend data response:', trendRes)
        if (trendRes && (trendRes.code === 200 || trendRes.code === 0 || trendRes.dates)) {
          const trendData = trendRes.data || trendRes
          this.chartData.trendData = {
            dates: trendData.dates || [],
            inData: trendData.inData || [],
            outData: trendData.outData || [],
            transferData: trendData.transferData || []
          }
          console.log('Updated trend data:', this.chartData.trendData)
        } else {
          console.error('获取趋势数据失败:', trendRes?.msg || trendRes?.message || '响应格式不正确')
          this.$message.error(trendRes?.msg || trendRes?.message || '获取趋势数据失败')
        }

        // 获取仓库分布数据
        const warehouseRes = await getWarehouseDistribution()
        console.log('Warehouse distribution response:', warehouseRes)
        if (warehouseRes && (warehouseRes.code === 200 || warehouseRes.code === 0 || warehouseRes.warehouseData)) {
          // 尝试从不同位置获取数据
          this.chartData.warehouseData = warehouseRes.data?.warehouseData || 
                                         warehouseRes.warehouseData || 
                                         warehouseRes.data || []
          console.log('Updated warehouse data:', this.chartData.warehouseData)
        } else {
          console.error('获取仓库分布数据失败:', warehouseRes?.msg || warehouseRes?.message || '响应格式不正确')
          this.$message.error(warehouseRes?.msg || warehouseRes?.message || '获取仓库分布数据失败')
        }

        // 数据加载完成后，在下一个DOM更新周期重新渲染图表
        this.$nextTick(() => {
          this.renderCharts()
        })
      } catch (error) {
        console.error('获取图表数据失败 (catch):', error)
        this.$message.error('获取图表数据失败')
      }
    },
    
    async loadTopTenData() {
      try {
        console.log('Loading top ten data...')
        const params = {}
        if (this.rankDateRange && this.rankDateRange.length === 2) {
          params.startDate = this.rankDateRange[0]
          params.endDate = this.rankDateRange[1]
        }
        
        const res = await getTopTenInOutData(params)
        console.log('Top ten data response:', res)
        
        if (res && (res.code === 200 || res.code === 0 || res.topTenData)) {
          // 尝试从不同位置获取数据
          this.topTenData = res.data?.topTenData || res.topTenData || []
          console.log('Updated top ten data:', this.topTenData)
        } else {
          console.error('获取前十排行数据失败:', res?.msg || res?.message || '响应格式不正确')
          this.$message.error(res?.msg || res?.message || '获取前十排行数据失败')
        }
      } catch (error) {
        console.error('获取前十排行数据失败 (catch):', error)
        this.$message.error('获取前十排行数据失败')
      }
    },
    
    onRankDateChange() {
      this.loadTopTenData()
    },
    
    renderCharts() {
      console.log('Rendering charts with data:', this.chartData)
      
      // 渲染趋势图
      if (this.chartData.trendData.dates && this.chartData.trendData.dates.length > 0) {
        const trendChart = echarts.init(document.getElementById('trendChart'))
        const trendOption = {
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['入库', '出库', '调货']
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: this.chartData.trendData.dates
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '入库',
              type: 'line',
              data: this.chartData.trendData.inData,
              itemStyle: { color: '#67C23A' },
              // 添加标记点，使数据点更明显
              symbolSize: 6,
              smooth: true
            },
            {
              name: '出库',
              type: 'line',
              data: this.chartData.trendData.outData,
              itemStyle: { color: '#F56C6C' },
              symbolSize: 6,
              smooth: true
            },
            {
              name: '调货',
              type: 'line',
              data: this.chartData.trendData.transferData,
              itemStyle: { color: '#E6A23C' },
              symbolSize: 6,
              smooth: true
            }
          ]
        }
        trendChart.setOption(trendOption)
      } else {
        console.log('趋势数据为空，无法渲染趋势图')
      }

      // 渲染仓库分布图
      if (this.chartData.warehouseData && this.chartData.warehouseData.length > 0) {
        const warehouseChart = echarts.init(document.getElementById('warehouseChart'))
        const warehouseOption = {
          tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
          },
          legend: {
            orient: 'vertical',
            left: 'left'
          },
          series: [
            {
              name: '仓库库存',
              type: 'pie',
              radius: ['40%', '70%'], // 环形图效果
              avoidLabelOverlap: false,
              data: this.chartData.warehouseData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        }
        warehouseChart.setOption(warehouseOption)
      } else {
        console.log('仓库分布数据为空，无法渲染仓库分布图')
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      
      let date = null
      if (typeof dateStr === 'string') {
        date = new Date(dateStr.replace('T', ' ').replace('Z', ''));
      } else {
        date = new Date(dateStr)
      }
      
      if (isNaN(date.getTime())) {
        return dateStr
      }
      
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    goToInOutRecord() {
      this.$router.push('/inoutrecord')
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dashboard-header h2 {
  margin: 0;
  color: #303133;
  font-weight: 500;
}

.dashboard-date {
  color: #909399;
  font-size: 14px;
}

.dashboard-cards {
  margin-bottom: 20px;
}

.card-item {
  border: none;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.card-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.card-content {
  display: flex;
  align-items: center;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 24px;
}

.bg-blue {
  background-color: #409EFF;
}

.bg-green {
  background-color: #67C23A;
}

.bg-orange {
  background-color: #E6A23C;
}

.bg-purple {
  background-color: #9013FE;
}

.card-info {
  flex: 1;
}

.card-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 5px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.dashboard-charts {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 8px;
  border: none;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}

.dashboard-ranking {
  margin-bottom: 20px;
}

.ranking-card {
  border-radius: 8px;
  border: none;
}

.date-filter {
  display: flex;
  align-items: center;
}

.date-filter .el-date-editor {
  width: auto;
}

.dashboard-recent {
  margin-bottom: 20px;
}

.recent-card {
  border-radius: 8px;
  border: none;
}
</style>