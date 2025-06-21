package com.jobportal.service.recruiter;

import com.jobportal.dto.request.CheckInfo;
import com.jobportal.dto.response.CompanyRes;
import com.jobportal.entity.Company;
import com.jobportal.entity.Intern;
import com.jobportal.entity.Recruiter;
import com.jobportal.repostory.CompanyRepository;
import com.jobportal.repostory.RecruiterRepository;
import com.jobportal.security.CustomDefaultOidcUser;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.OnlineService;
import com.jobportal.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CompanyService {
    CompanyRepository companyRepository;
    OnlineService onlineService;
    UserService userService;
    RecruiterRepository recruiterRepository;

    private Company getCompany(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer id = null;
        if(principal instanceof CustomUserDetails customUserDetails)
            id = customUserDetails.getId();
        else if (principal instanceof CustomDefaultOidcUser customDefaultOidcUser)
            id = customDefaultOidcUser.getUser().getId();
        return companyRepository.findById(id).get();
    }

    public Page<Company> getAllCompanies(int page, int size){
        return companyRepository.getCompaniesApproved(PageRequest.of(page-1, size));
    }

    public List<Company> getAllCompanies(){
        return companyRepository.findAll();
    }

    public CompanyRes getCompanyInformation(int id){
        Company company = companyRepository.findById(id).get();
        CompanyRes companyRes = new CompanyRes();
        companyRes.setId(company.getId());
        companyRes.setCompanyName(company.getCompanyName());
        companyRes.setCompanyWebsite(company.getWebsite());
        if(company.getCompanySize() != null)
            companyRes.setCompanySize(company.getCompanySize().getDisplayName());

        companyRes.setDistrict(company.getDistrict().getDisplayName()+", TpHCM");
        if(company.getImageUrl()!=null)
            companyRes.setImage("/images/companies/"+company.getId()+"/"+company.getImageUrl());

        companyRes.setDescription(company.getDescription());

        companyRes.setCompanyPhone(company.getNumberPhone());
        companyRes.setCompanyEmail(company.getEmail());
        companyRes.setCompanyAddress(company.getAddress());
        companyRes.setTaxCode(company.getTaxCode());

        companyRes.setCertificate("/images/certificate/"+company.getId()+"/"+company.getCertificate());
        if(company.getCheckInformation()!=null){
            if(company.getCheckInformation().equals(Boolean.TRUE))
                companyRes.setCheckInfor("yes");
            else if (company.getCheckInformation().equals(Boolean.FALSE))
                companyRes.setCheckInfor("no");
        }
        if(company.getCheckCertificate()!=null){
            if(company.getCheckCertificate().equals(Boolean.TRUE))
                companyRes.setCheckCer("yes");
            else if (company.getCheckCertificate().equals(Boolean.FALSE))
                companyRes.setCheckCer("no");
        }

        return companyRes;
    }

    public void updateInfo(CheckInfo checkInfo){
        Company c = companyRepository.findById(checkInfo.getId()).get();
        c.setCheckInformation(checkInfo.getCheck());
        Recruiter r = recruiterRepository.findById(checkInfo.getId()).get();
        String content = "<em>Thông tin công ty</em> của bạn đã được <strong>phê duyệt</strong> thành công";
        if(checkInfo.getCheck()==null)
            content = "<em>Thông tin công ty</em> của bạn đã bị <strong>từ chối</strong>";
        userService.notification(
                content,
                "/recruiter/setting?tab=tab4",
                "/images/admin/img.png",
                null,
                r
        );
    }
    public void updateCer(CheckInfo checkInfo){
        Company c = companyRepository.findById(checkInfo.getId()).get();
        c.setCheckCertificate(checkInfo.getCheck());
        Recruiter r = recruiterRepository.findById(checkInfo.getId()).get();
        String content = "<em>Chứng chỉ công ty</em> của bạn đã được <strong>phê duyệt</strong> thành công";
        if(checkInfo.getCheck()==null)
            content = "<em>Chứng chỉ công ty</em> của bạn đã bị <strong>từ chối</strong>";
        userService.notification(
                content,
                "/recruiter/setting?tab=tab3",
                "/images/admin/img.png",
                null,
                r
        );
    }
}
