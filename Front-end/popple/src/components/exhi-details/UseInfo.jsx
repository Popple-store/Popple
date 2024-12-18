import MiniStatistics from "../charts/MiniStatistics";
import {
  FaDog,
  FaUserSlash,
  FaArrowUp19,
} from "react-icons/fa6";
import { MdNoFood } from "react-icons/md";
import { CiWifiOff } from "react-icons/ci";
import {
  LuParkingCircleOff,
  LuCameraOff,
} from "react-icons/lu";
import { TbCurrencyDollarOff } from "react-icons/tb";
import MDEditor from "@uiw/react-md-editor";
import Insta from "../../assets/instagram.png";
import Home from "../../assets/home.png";

const escapeHtml = (text) => {
  return text?.replace(/</g, "&lt;").replace(/>/g, "&gt;");
};


export default function UseInfo({ data }) {
  const imageURL = import.meta.env.VITE_EXHIBITION_IMAGE;
  const h1Style = "font-bold text-[1.25rem]";
  const innerInfo = "m-6";

  const handleExternalNavigation = (url) => {
    window.open(url, "_blank");
  };

  function getMarkdownText(text) {
    // 이모지를 감지하기 위한 정규식
    const emojiRegex = /((?![\u{23}-\u1F6F3]([^\u{FE0F}]|$))\p{Emoji}(?:(?!\u{200D})\p{EComp}|(?=\u{200D})\u{200D}\p{Emoji})*)/gu;
    
    // 이모지 앞뒤로 공백 추가
    return text?.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replace(emojiRegex, ' $1 ');
  }

  const renderDescriptionImage = () => {
    return data.descriptionImage.map(r => (
      <img src={`${imageURL}${r}`} alt="설명 이미지" className="mb-2" />
    ))
  }

  const sections = [
    { title: "관람정보", key: "descriptionImage", content: renderDescriptionImage() },
    { title: "공지사항", key: "notice", content: <MDEditor.Markdown source={escapeHtml(getMarkdownText(data.notice))} /> },
    { title: "상세정보", key: "detailDescription", content: <MDEditor.Markdown source={escapeHtml(getMarkdownText(data.detailDescription))} /> },
    { title: "방문통계", key: "statistics", content: <MiniStatistics /> },
    { title: "링크", key: "link", content: (
      <div className="flex gap-4 mt-6">
        {data.homepageLink && (
          <img
            src={Home}
            alt="홈페이지 아이콘"
            className="w-10 h-10 cursor-pointer object-contain inline"
            onClick={() => handleExternalNavigation(data.homepageLink)}
          />
        )}
        {data.instagramLink && (
          <img
            src={Insta}
            alt="인스타그램 아이콘"
            className="w-10 h-10 cursor-pointer object-contain inline ml-3 mt-0.5"
            onClick={() => handleExternalNavigation(data.instagramLink)}
          />
        )}
        {(!data.homepageLink && !data.instagramLink) && (
          <p className="text-500">관련 링크가 없습니다.</p>
        )}
      </div>
    )},
  ];
  // 방문 통계
  // 남여 비율은 백엔드에서 계산하여 % 값만 front로 던져줌

  // 나이대별 비율도 100% 기준 10대부터 %값으로 주어짐
  return (
    <div className="flex flex-col gap-8 mb-[2rem] mx-12 mt-12 h-full">
      <div className="">
        <h1 className={h1Style}>주의사항!</h1>
        <IconBox data={data}/>
      </div>
      {sections.map((section, index) => (
        (data[section.key] !== "" && data[section.key] !== null)  && (
          <div key={index}>
            <h1 className={h1Style}>{section.title}</h1>
            <div className={innerInfo}>{section.content}</div>
          </div>
        )
      ))}
    </div>
  );
};

function IconBox({ data }) {;
  //
  const spanStyle = "w-[95px]";
  const iconStyle = "text-popple w-full flex flex-col items-center";
  const innerIconStyle = "size-[36px] mb-2";
  const innerTextStyle = "text-[12px] text-center";
  return (
    <div className="flex mt-5 gap-3">
      {/* {data.fee === "0" ? <LuParkingCircle /> : <LuParkingCircleOff />} */}
      {!data.food && 
      <span className={spanStyle}>
        <div className={iconStyle}><MdNoFood className={innerIconStyle}/><span className={innerTextStyle}>음식물<br />반입금지</span></div>
      </span>}
      {!data.grade &&  
      <span className={spanStyle}>
        <div className={iconStyle}><FaArrowUp19 className={innerIconStyle}/><span className={innerTextStyle}>청소년 관람 불가</span></div>
      </span>}
      {!data.kids &&  
      <span className={spanStyle}>
        <div className={iconStyle}><FaUserSlash className={innerIconStyle}/><span className={innerTextStyle}>노키즈존</span></div>
      </span>}
      {!data.wifi &&  
      <span className={spanStyle}>
        <div className={iconStyle}><CiWifiOff className={innerIconStyle}/><span className={innerTextStyle}>와이파이 불가</span></div>
      </span>}
      {!data.park &&  
        <span className={spanStyle}>
          <div className={iconStyle}><LuParkingCircleOff className={innerIconStyle}/><span className={innerTextStyle}>주차 불가</span></div>
        </span>}
      {!data.pet &&  
      <span className={spanStyle}>
        <div className={iconStyle}><FaDog className={innerIconStyle}/><span className={innerTextStyle}>반려동물<br />동반가능</span></div>
      </span>}
      {!data.camera &&  
      <span className={spanStyle}>
        <div className={iconStyle}><LuCameraOff className={innerIconStyle}/><span className={innerTextStyle}>카메라 불가</span></div>
      </span>}
      {data.fee === "0" &&  
      <span className={spanStyle}>
        <div className={iconStyle}><TbCurrencyDollarOff className={innerIconStyle}/><span className={innerTextStyle}>입장료 무료</span></div>
      </span>}
    </div>
  );
};

