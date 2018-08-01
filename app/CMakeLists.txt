cmake_minimum_required(VERSION 3.4.1)

add_library(
             hidraw-lib

             SHARED

             src/main/cpp/hidraw-lib.cpp )

find_library(
              log-lib

              log )

target_link_libraries(
                       hidraw-lib

                       ${log-lib} )