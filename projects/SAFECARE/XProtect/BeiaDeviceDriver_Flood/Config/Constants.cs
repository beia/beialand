using System;
using VideoOS.Platform.DriverFramework.Definitions;

namespace Safecare.BeiaDeviceDriver_Flood
{
    public static class Constants
    {
        public static readonly ProductDefinition Product1 = new ProductDefinition
        {
            Id = new Guid("75FE3C48-C2FE-49D0-8A4F-54F350D82911"),
            Name = "BeiaDeviceDriver_Flood"
        };

        public static readonly Guid DriverId = new Guid("4927B8EB-134F-4E7A-934C-E871D4EE5E4F");
       
        public static readonly Guid Audio1 = new Guid("7f14df13-e1ed-48ab-bc0c-911be19d193e");
        public static readonly Guid Video1 = new Guid("ba43cf33-bf9a-4c59-9c5f-46dad7fc08f9");
        public static readonly Guid Input1 = new Guid("182ded1d-4c5f-4471-921b-fa4ccf2dcf6a");
        public static readonly Guid Metadata1 = new Guid("adc12b79-0240-4940-a969-6f815384ada7");
        public static readonly Guid Output1 = new Guid("76da8f9d-52ba-409b-a9b8-c6cdb6e8e7a4");
        public static readonly Guid Speaker1 = new Guid("80263a0f-d76a-4f3e-ba49-4254828cc83c");

        public static readonly Guid VideoStream1RefId = new Guid("59630068-73cf-45bd-ae47-d443212d994f");
        public static readonly Guid AudioStream1RefId = new Guid("555c4eee-26f0-427f-8a67-927066fd5612");
        public static readonly Guid SpeakerStream1RefId = new Guid("14E3C441-726B-41F7-B375-20DEA46EB744");
    }
}
