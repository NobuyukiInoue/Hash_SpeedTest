#include <assert.h>
#include <chrono>
#include <cstdio>
#include <iostream>
#include <limits.h>
#include <mutex>
#include <thread>
#include <vector>

std::mutex mtx_;
size_t count_ = 0;

void add_count(size_t num)
{
    std::lock_guard<std::mutex> lock(mtx_);
    count_ += num;
}

void worker(size_t num_thread)
{
    const size_t loop = UINT_MAX / num_thread;
    size_t sum = 0;
    for(size_t i=0; i<loop; ++i){
        ++sum;
    }

    add_count(sum);
}

int main(int argc, char *argv[])
{
    // スレッド数 デフォルトは1
    size_t num_thread = 1;

    // コマンドラインからスレッドの本数を読み込む
    if(argc >= 2){
        size_t val = strtoul(argv[1], NULL, 10);
        if(val == 0){
            // 変換失敗時はスレッド数1で計測
        }else{
            num_thread = val;
            std::cout << "Number of threads " << num_thread << " : ";
        }
    }

    // 余りを求めておく
    size_t mod = UINT_MAX % num_thread;

    // 計測開始
    auto start = std::chrono::high_resolution_clock::now();

    std::vector<std::thread> threads;

    // スレッドを生成して実行開始
    for(size_t i=0; i<num_thread; ++i){
        threads.emplace_back(std::thread(worker,num_thread));
    }

    for(auto& thread : threads){
        thread.join();
    }

    // 余りを足しておく
    add_count(mod);

    // 計測完了
    auto end = std::chrono::high_resolution_clock::now();
    auto dur = end - start;
    auto msec = std::chrono::duration_cast<std::chrono::milliseconds>(dur).count();
    assert(count_ == UINT_MAX);
    std::cout << msec << "msec\n";

    return 0;
}

