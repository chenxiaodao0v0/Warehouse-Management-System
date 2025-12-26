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
              <div class="card-value">{{ stats.enterpriseCount }}</div>
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

export default {
  name: 'Dashboard',
  data() {
    return {
      currentDate: '',
      stats: {
        goodsCount: 0,
        warehouseCount: 0,
        inOutRecordCount: 0,
        enterpriseCount: 1 // 企业信息一般只有一个
      },
      recentRecords: [],
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
    this.getCurrentDate()
    this.loadStats()
    this.loadRecentRecords()
    this.loadChartData()
    this.$nextTick(() => {
      this.renderCharts()
    })
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
      // 这里应该调用实际的API获取统计数据
      // 暂时使用模拟数据
      this.stats = {
        goodsCount: 128,
        warehouseCount: 5,
        inOutRecordCount: 326,
        enterpriseCount: 1
      }
    },
    async loadRecentRecords() {
      // 这里应该调用API获取最近的出入库记录
      // 暂时使用模拟数据
      this.recentRecords = [
        { id: 'R100001', goodsId: 'G1001', warehouseId: 'W001', type: 1, quantity: 50, contactPerson: '张三', operateTime: '2025-12-20T09:03:50.000+00:00' },
        { id: 'R100002', goodsId: 'G1002', warehouseId: 'W002', type: 2, quantity: 20, contactPerson: '李四', operateTime: '2025-12-21T10:15:30.000+00:00' },
        { id: 'R100003', goodsId: 'G1003', warehouseId: 'W001', type: 3, quantity: 30, contactPerson: '王五', operateTime: '2025-12-22T14:20:45.000+00:00' },
        { id: 'R100004', goodsId: 'G1004', warehouseId: 'W003', type: 1, quantity: 80, contactPerson: '赵六', operateTime: '2025-12-23T11:30:12.000+00:00' },
        { id: 'R100005', goodsId: 'G1005', warehouseId: 'W002', type: 2, quantity: 15, contactPerson: '钱七', operateTime: '2025-12-24T16:45:28.000+00:00' }
      ]
    },
    async loadChartData() {
      // 模拟图表数据
      const dates = []
      const inData = []
      const outData = []
      const transferData = []
      
      for (let i = 6; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        dates.push(`${date.getMonth()+1}-${date.getDate()}`)
        inData.push(Math.floor(Math.random() * 20) + 5)
        outData.push(Math.floor(Math.random() * 15) + 3)
        transferData.push(Math.floor(Math.random() * 10) + 2)
      }
      
      this.chartData.trendData = {
        dates,
        inData,
        outData,
        transferData
      }
      
      // 仓库分布数据
      this.chartData.warehouseData = [
        { name: '仓库A', value: 120 },
        { name: '仓库B', value: 85 },
        { name: '仓库C', value: 95 },
        { name: '仓库D', value: 70 },
        { name: '仓库E', value: 60 }
      ]
    },
    renderCharts() {
      // 渲染趋势图
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
            itemStyle: { color: '#67C23A' }
          },
          {
            name: '出库',
            type: 'line',
            data: this.chartData.trendData.outData,
            itemStyle: { color: '#F56C6C' }
          },
          {
            name: '调货',
            type: 'line',
            data: this.chartData.trendData.transferData,
            itemStyle: { color: '#E6A23C' }
          }
        ]
      }
      trendChart.setOption(trendOption)

      // 渲染仓库分布图
      const warehouseChart = echarts.init(document.getElementById('warehouseChart'))
      const warehouseOption = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '仓库库存',
            type: 'pie',
            radius: '50%',
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

.dashboard-recent {
  margin-bottom: 20px;
}

.recent-card {
  border-radius: 8px;
  border: none;
}
</style>