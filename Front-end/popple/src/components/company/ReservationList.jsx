import { useEffect, useState } from "react";
import { reservationAPI } from "../../api/services/Reservation";
import { FaUserCheck } from "react-icons/fa";
import { GiCrossMark } from "react-icons/gi";
import SimpleInfoBox from "./SimpleInfoBox";

export default function ReservationList({ event, eventId, onClose }) {
  // 예약자 명단
  const [reserverList, setReserverList] = useState([]);

  const getReservationList = async () => {
    const res = await reservationAPI.getReserverList(eventId);
    setReserverList(res.data);
  };

  useEffect(() => {
    getReservationList();
  }, [])

  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 상태
  const itemsPerPage = 10; // 한 페이지에 보여줄 항목 수

  // 페이지별로 현재 보여줄 항목들 계산
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = reserverList.slice(startIndex, startIndex + itemsPerPage);
  const totalPages = Math.ceil(reserverList.length / itemsPerPage);

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  // 예약 방문 상태 반환 함수
  function getStatus(attend) {
    if (attend) {
      return { value: <FaUserCheck />, color: 'text-green-500' }; // true일 때 초록색 참석
    } else {
      return { value: <GiCrossMark />, color: 'text-red-400' };   // false일 때 빨간색 불참
    }
  }

  // 날짜 포멧 변경 함수
  function dateToString(arr) {
    const [y,m,d] = arr;
    return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
  }

  const trStyle = "grid grid-cols-[0.5fr_1.5fr_1.5fr_1fr_1fr] border-b my-auto";
  const thStyle = "my-auto";

  return (
    <div>
      <SimpleInfoBox info={event} />
      <div className="mt-8 pb-12 mb-12">
          <h2 className="flex justify-between items-center text-[16px]">
            <div className="inline">
              총 <span className="font-bold text-[24px] text-popple-light">{currentItems.length}</span>개의 예약이 있습니다.
            </div>
            <span className="text-[#727171]">
              <FaUserCheck className="inline text-green-500" /> : 방문 완료, <GiCrossMark className="inline text-red-400" /> : 방문 전
            </span>  
          </h2>
          <hr  className=" border-gray-500"/>
          <table className="w-full mt-3 text-left">
            <thead>
              <tr className={`${trStyle} border-gray-500 h-12`}>
                <th className="text-center">No.</th>
                <th className="text-center">예약자명</th>
                <th className="text-center">이메일</th>
                <th className="text-center">예약 일자</th>
                <th className="text-center">상태</th>
              </tr>
            </thead>
            <tbody>
              {currentItems.map((reserve, index) => {
                const { value, color } = getStatus(reserve.attend);
                return (
                  <tr key={reserve.id} className={`${trStyle} h-8`}>
                    <td className={`${thStyle} m-auto`}>{index+1}</td>
                    <td className={`${thStyle} m-auto`}>{reserve.reserverName}</td>
                    <td className={`${thStyle} m-auto`}>{reserve.email}</td>
                    <td className={`${thStyle} m-auto`}>{dateToString(reserve.reserveTime)}</td>
                    <td className={`${thStyle} ${color} m-auto`}>{value}</td>
                  </tr>
                );
              })}
              
            </tbody>
          </table>
          {/* 페이지네이션 버튼 10개가 넘지 않으면 페이지네이션 버튼 안보임*/}
          {totalPages > 1 && (
            <div className="flex justify-center mt-4">
              {[...Array(totalPages)].map((_, index) => (
                <button
                  key={index}
                  onClick={() => handlePageChange(index + 1)}
                  className={`mx-1 px-2 py-1 rounded ${currentPage === index + 1 ? "bg-popple-light text-white" : "bg-gray-300 text-black"}`}
                >
                  {index + 1}
                </button>
              ))}
            </div>
          )}
        </div>
    </div>
  );
};