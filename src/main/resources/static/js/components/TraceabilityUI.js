const TraceabilityUI = {
    template: `
        <div>
            <el-card class="box-card">
                <template #header>
                    <div class="card-header">
                        <span>产品溯源查询</span>
                    </div>
                </template>
                <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                    <el-form-item label="产品ID" prop="productId">
                        <el-input v-model="form.productId" placeholder="请输入产品ID"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch" :loading="loading">查询</el-button>
                        <el-button @click="handleScan">
                            <el-icon><Camera /></el-icon>
                            扫描二维码
                        </el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <el-card v-if="tableData.length > 0" class="box-card" style="margin-top: 20px;">
                <template #header>
                    <div class="card-header">
                        <span>溯源结果</span>
                    </div>
                </template>
                <el-table :data="tableData" style="width: 100%" v-loading="loading">
                    <el-table-column prop="productId" label="产品ID"></el-table-column>
                    <el-table-column prop="productionDate" label="生产日期"></el-table-column>
                    <el-table-column prop="productionLocation" label="生产地点"></el-table-column>
                    <el-table-column prop="transportRecords" label="运输记录"></el-table-column>
                </el-table>
            </el-card>
        </div>
    `,
    setup() {
        const { ref, reactive } = Vue;
        const { ElMessage } = ElementPlus;

        const formRef = ref(null);
        const loading = ref(false);
        const tableData = ref([]);

        const form = reactive({
            productId: ''
        });

        const rules = {
            productId: [
                { required: true, message: '请输入产品ID', trigger: 'blur' }
            ]
        };

        const handleSearch = async () => {
            if (!formRef.value) return;
            
            await formRef.value.validate(async (valid) => {
                if (valid) {
                    try {
                        loading.value = true;
                        const response = await fetch('/api/traceability/search', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(form)
                        });
                        const data = await response.json();
                        if (data.success) {
                            tableData.value = data.data;
                        } else {
                            ElMessage.error(data.message || '查询失败');
                        }
                    } catch (error) {
                        ElMessage.error('系统错误');
                        console.error(error);
                    } finally {
                        loading.value = false;
                    }
                }
            });
        };

        const handleScan = () => {
            ElMessage.info('请使用手机扫描二维码');
        };

        return {
            formRef,
            form,
            rules,
            loading,
            tableData,
            handleSearch,
            handleScan
        };
    }
}; 