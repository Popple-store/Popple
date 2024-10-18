import { useState } from "react";
import company from "../assets/company.png";
import sunday from "../assets/week/sunday.png";
import monday from "../assets/week/monday.png";
import tuesday from "../assets/week/tuesday.png";
import wednesday from "../assets/week/wednesday.png";
import thursday from "../assets/week/thursday.png";
import friday from "../assets/week/friday.png";
import saturday from "../assets/week/saturday.png";

import {
  LuParkingCircle,
  LuParkingCircleOff,
  LuCamera,
  LuCameraOff,
} from "react-icons/lu";
import { MdFastfood, MdNoFood } from "react-icons/md";
import { CiWifiOn, CiWifiOff } from "react-icons/ci";
import { useFetcher } from "react-router-dom";
import { CgMonday } from "react-icons/cg";
export default function ExhibitionRegist() {
  const [parking, setParking] = useState(false);
  const handleParking = () => {
    setParking(!parking);
  };

  const [food, setFood] = useState(false);
  const handleFood = () => {
    setFood(!food);
  };

  const [wifi, setWifi] = useState(false);
  const handleWifi = () => {
    setWifi(!wifi);
  };

  const [camera, setCamera] = useState(false);
  const handleCamera = () => {
    setCamera(!camera);
  };

  const [weekFeild, setWeekFeild] = useState([
    {
      d: 1,
      label: "일요일",
      src: sunday,
      alt: "일요일",
    },
    {
      d: 2,
      label: "월요일",
      src: monday,
      alt: "월요일",
    },
    {
      d: 3,

      label: "화요일",
      src: tuesday,
      alt: "화요일",
    },
    {
      d: 4,
      label: "수요일",
      src: wednesday,
      alt: "수요일",
    },
    {
      d: 5,
      label: "목요일",
      src: thursday,
      alt: "목요일",
    },
    {
      d: 6,
      label: "금요일",
      src: friday,
      alt: "금요일",
    },
    {
      d: 7,
      label: "토요일",
      src: saturday,
      alt: "토요일",
    },
  ]);
  const handleWeek = (w) => {
    setWeek(w)
  }
  const [week, setWeek] = useState("");
  
  return (
    <>
      <p className="text-lg mb-2">팝업/전시 등록</p>
      <hr className="w-full" />
      <div className="mt-2">기본 정보</div>
      <div className="flex flex-col w-5/6 mx-auto gap-5">
        <div>
          <div className="grid grid-cols-2 gap-x-20 h-full">
            <div className="flex flex-col gap-5">
              <div>
                <label className="text-sm">
                  팝업/전시명<span className="text-red-500">*</span>
                </label>
                <input
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                  placeholder="팝업/전시명을 입력해주세요"
                />
              </div>
              <div>
                <label className="text-sm  ">
                  부제<span className="text-red-500">*</span>
                </label>
                <input
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                  placeholder="부제목을 입력해주세요"
                />
              </div>
              <div>
                <label className="text-sm ">입장료</label>
                <input
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                  placeholder="입장료를 입력해주세요"
                />
              </div>
              <div>
                <label className="text-sm">
                  전시기간<span className="text-red-500">*</span>
                </label>
                <div className="">
                  <input
                    type="date"
                    className="bg-gray-50 border rounded-lg inline w-1/3 p-2.5 text-xs"
                    required
                    placeholder="시작일"
                  />
                  <span>~</span>
                  <input
                    type="date"
                    className="bg-gray-50 border rounded-lg inline w-1/3 p-2.5 text-xs"
                    required
                    placeholder="종료일"
                  />
                </div>
              </div>
              <div>
                <label htmlFor="notice" className="text-sm">
                  공지사항
                </label>
                <div>
                  <input
                    placeholder="마크다운이 들어갈 곳"
                    id="notice"
                    type="textarea"
                    className="bg-gray-50 border rounded-lg inline w-full p-2.5"
                  />
                </div>
              </div>
            </div>

            <div className="flex flex-col justify-between">
              <div>
                <label className="text-sm block">
                  장소<span className="text-red-500">*</span>
                </label>
                <input
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg w-2/3 p-2.5 inline "
                  placeholder="도로명주소"
                />
                <button className="border p-2 rounded-lg">검색</button>
                <input
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                  placeholder="상세주소"
                />
              </div>
              <div>
                <label className="text-sm">홈페이지 링크</label>
                <input
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                  placeholder="홈페이지 링크(URL)을 입력해주세요"
                />
              </div>
              <div>
                <label className="text-sm">인스타그램 링크</label>
                <input
                  className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5"
                  placeholder="인스타그램 링크(URL)을 입력해주세요"
                />
              </div>
              <div>
                <label htmlFor="description" className="text-sm">
                  상세설명
                </label>
                <div>
                  <input
                    placeholder="마크다운이 들어갈 곳"
                    id="description"
                    type="textarea"
                    className="bg-gray-50 border rounded-lg inline w-full p-2.5"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div>
          관람시간 정보<span className="text-red-500">*</span>
          <div className="mt-5 flex justify-between">
            {weekFeild.map((w) => {
              return(
                <img src={w.src} alt={w.alt} className="w-20 hover:cursor-pointer" onClick={()=>{
                  handleWeek(w.label);
                }}/>
              )
            })}
          </div>
        </div>
        <div className="flex justify-center">
          <div className="border w-80 h-36 rounded-lg">
            <div className="flex w-full justify-between p-3">
              {/* {week.filter((w)=>{
                return (
                  <div>{w.label}</div>
                )
              })
              
              } */}
              <div className="text-xs">
                <input type="checkbox" />
                <span>휴무지정</span>
              </div>
            </div>
            <div className="flex justify-between p-3 ">
              <label className="block text-xs text-center" htmlFor="open">
                오픈
              </label>
              <input
                type="time"
                id="open"
                className="w-24 border rounded-lg text-[0.8rem]"
              />
              <label className="block text-xs text-center" htmlFor="close">
                마감
              </label>
              <input
                type="time"
                id="close"
                className="w-24 rounded-lg border text-[0.8rem]"
              />
            </div>
            <div className="flex m-2 justify-end">
              <button
                type="submit"
                className="border text-sm p-1 rounded-lg hover:bg-popple hover:text-white"
              >
                완료
              </button>
            </div>
          </div>
        </div>

        <div className="mt-10">
          제약사항<span className="text-red-500">*</span>
          <div className="grid grid-cols-4 gap-1 w-3/5">
            <div onClick={handleParking} className="mt-5">
              {parking ? (
                <div>
                  <div className="flex justify-center">
                    <LuParkingCircleOff
                      style={{ color: "red", fontSize: "30px" }}
                    />
                  </div>
                  <div className="flex justify-center">
                    <label className="text-red-500 text-xs font-bold">
                      {" "}
                      주차 금지{" "}
                    </label>
                  </div>
                </div>
              ) : (
                <div>
                  <div className="flex justify-center">
                    <LuParkingCircle style={{ fontSize: "30px" }} />
                  </div>
                  <div className="flex justify-center">
                    <label className=" text-xs font-bold"> 주차 가능 </label>
                  </div>
                </div>
              )}
            </div>
            <div onClick={handleFood} className="mt-5">
              {food ? (
                <div>
                  <div className="flex justify-center">
                    <MdNoFood style={{ color: "red", fontSize: "30px" }} />
                  </div>
                  <div className="flex justify-center">
                    <label className="text-red-500 text-xs font-bold">
                      음식물 반입 금지
                    </label>
                  </div>
                </div>
              ) : (
                <div>
                  <div className="flex justify-center">
                    <MdFastfood style={{ fontSize: "30px" }} />
                  </div>
                  <div className="flex justify-center">
                    <label className=" text-xs font-bold">
                      {" "}
                      음식물 반입 가능{" "}
                    </label>
                  </div>
                </div>
              )}
            </div>
            <div onClick={handleWifi} className="mt-5">
              {wifi ? (
                <div>
                  <div className="flex justify-center">
                    <CiWifiOff style={{ color: "red", fontSize: "30px" }} />
                  </div>
                  <div className="flex justify-center">
                    <label className="text-red-500 text-xs font-bold">
                      와이파이 불가
                    </label>
                  </div>
                </div>
              ) : (
                <div>
                  <div className="flex justify-center">
                    <CiWifiOn style={{ fontSize: "30px" }} />
                  </div>
                  <div className="flex justify-center">
                    <label className=" text-xs font-bold">
                      {" "}
                      와이파이 가능{" "}
                    </label>
                  </div>
                </div>
              )}
            </div>
            <div onClick={handleCamera} className="mt-5">
              {camera ? (
                <div>
                  <div className="flex justify-center">
                    <LuCameraOff style={{ color: "red", fontSize: "30px" }} />
                  </div>
                  <div className="flex justify-center">
                    <label className="text-red-500 text-xs font-bold">
                      촬영 금지
                    </label>
                  </div>
                </div>
              ) : (
                <div>
                  <div className="flex justify-center">
                    <LuCamera style={{ fontSize: "30px" }} />
                  </div>
                  <div className="flex justify-center">
                    <label className=" text-xs font-bold">촬영 가능 </label>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
        <div>
          <hr className="w-full mt-10" />
          <div className="flex justify-end items-center">
            <button type="submit" className="border rounded-lg p-3 mt-10">
              다음
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
//https://www.flaticon.com/kr/free-icon/monday_8144169 요일 아이콘
