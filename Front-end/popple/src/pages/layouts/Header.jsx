import { useNavigate } from "react-router-dom";
import "./Header.module.css";
import { useLoginStore } from "../../stores/LoginState";
import { useEffect } from "react";
import { getCookie, removeCookie } from "../../utils/CookieUtils";
import { useLoginUserStore } from "../../stores/LoginUserState";
import { jwtDecode } from "jwt-decode";

export default function Header() {
  const { isLoggedIn, setIsLoggedIn } = useLoginStore(state => state);
  const { loginUserNickname, loginUserRole, setLoginUser } = useLoginUserStore(state => state);

  const checkLoggedIn = () => {
    const token = getCookie("accessToken");
    if (token) {
      setIsLoggedIn(true);
      setLoginUser(jwtDecode(token).name, jwtDecode(token).nickname, jwtDecode(token).role);
    } else {
      setIsLoggedIn(false);
    }
  };

  useEffect(() => {
    checkLoggedIn();
  }, []);
  
  const navigate = useNavigate();

  const handleNavigation = (path) => {
    navigate(path);
  }

  const handleLogout = () => {
    setIsLoggedIn(false);
    setLoginUser(null);
    removeCookie("accessToken");
    window.location.reload("/");
    console.log("로그아웃");
  }

  return (
    <header className="fixed h-16 top-0 w-full bg-white border-b border-gray-300 z-50">
      <div className="grid grid-cols-[110px_1fr_130px] sm:grid-cols-[110px_1fr_150px] md:grid-cols-[110px_1fr_280px] lg:grid-cols-[280px_1fr_280px] xl:px-48 w-full">
        {/* 헤더 로고 */}
        <div className="p-2.5">
          <img src="/popple_logo(2).png" alt="메인 로고" onClick={() => handleNavigation("/")} className="list-none cursor-pointer w-[90px] h-auto"/>
        </div>
        {/* 헤더 메뉴 */}
        <div className="flex pl-[10px] sm:pl-[80px] md:pl-[50px] lg:pl-0 gap-1 sm:gap-2 lg:gap-8 text-base md:text-xl justify-center items-center whitespace-nowrap">
          { loginUserRole === "ROLE_ADMIN" || loginUserRole === "ROLE_COMPANY" ? 
            <>
              <li onClick={() => handleNavigation("/pop-up")} className="list-none cursor-pointer">
                팝업
              </li>
              <li onClick={() => handleNavigation("/exhibition")} className="list-none cursor-pointer">전시회</li>
              <li onClick={() => handleNavigation("/event")} className="list-none cursor-pointer">EVENT</li>
              <li onClick={() => handleNavigation("/regist")} className="list-none cursor-pointer">팝업/전시등록</li>
            </>
            :
            <>
              <li onClick={() => handleNavigation("/pop-up")} className="list-none cursor-pointer">
                팝업
              </li>
              <li onClick={() => handleNavigation("/exhibition")} className="list-none cursor-pointer">전시회</li>
              <li onClick={() => handleNavigation("/event")} className="list-none cursor-pointer">EVENT</li>
            </>
          }
        </div>
        {/* 헤더 로그인 관련 */}
        <div className="flex justify-end items-center text-xs md:text-sm text-gray-600 divide-x divide-gray-400 whitespace-nowrap">
          { isLoggedIn ? 
            <>
              <li onClick={() => handleNavigation("/my-page")} className="list-none cursor-pointer px-1">{loginUserNickname}</li>
              <li onClick={() => handleLogout()} className="list-none cursor-pointer px-1">
                LOGOUT
              </li>
              <li onClick={() => handleNavigation("/my-page")} className="hidden md:block list-none cursor-pointer px-1">{loginUserRole === "ROLE_COMPANY" ? "COMPANY" : "마이페이지"}</li>
            </>
            :
            <>
              <li onClick={() => handleNavigation("/login")} className="list-none cursor-pointer px-1">
              LOGIN
              </li>
            </>
          }
          <li onClick={() => handleNavigation("/help")} className="hidden md:block list-none cursor-pointer px-1">HELP</li>
        </div>
      </div>
    </header>
  );
};
