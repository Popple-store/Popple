import { useDaumPostcodePopup } from "react-daum-postcode";

export default function PostCode({ className, value, setAddress }) {
  const open = useDaumPostcodePopup();

  const handleComplete = (data) => {
    console.log(data);
    
    // let fullAddress = data.address;
    // let extraAddress = '';

    // if (data.addressType === 'R') {
    //   if (data.bname !== '') {
    //     extraAddress += data.bname;
    //   }
    //   if (data.buildingName !== '') {
    //     extraAddress += extraAddress !== '' ? `, ${data.buildingName}` : data.buildingName;
    //   }
    //   fullAddress += extraAddress !== '' ? ` (${extraAddress})` : '';
    // }

    // console.log(fullAddress); // e.g. '서울 성동구 왕십리로2길 20 (성수동1가)'
    const { jibunAddress, roadAddress } = data;
    setAddress({ jibunAddress, roadAddress });
  };

  const handleClick = () => {
    open({ onComplete: handleComplete });
  };

  return (
    <button type='button' className={className} onClick={handleClick}>
      {value}
    </button>
  );
};